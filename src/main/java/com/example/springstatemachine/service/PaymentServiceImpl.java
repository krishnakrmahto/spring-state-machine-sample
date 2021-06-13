package com.example.springstatemachine.service;

import com.example.springstatemachine.domain.Payment;
import com.example.springstatemachine.domain.PaymentEvent;
import com.example.springstatemachine.domain.PaymentState;
import com.example.springstatemachine.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  public static final String PAYMENT_ID_HEADER = "payment_id";

  private final PaymentRepository repository;
  private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
  private final PaymentStateChangeInterceptor stateChangeInterceptor;

  @Transactional
  @Override
  public Payment newPayment(Payment payment) {
    payment.setState(PaymentState.NEW);
    return repository.save(payment);
  }

  @Transactional
  @Override
  public StateMachine<PaymentState, PaymentEvent> preAuthorize(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
    sendEvent(paymentId, stateMachine, PaymentEvent.PRE_AUTHORIZE);
    return stateMachine;
  }

  @Transactional
  @Override
  public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
    sendEvent(paymentId, stateMachine, PaymentEvent.AUTHORIZE);
    return stateMachine;
  }

  @Deprecated
  @Transactional
  @Override
  public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
    sendEvent(paymentId, stateMachine, PaymentEvent.AUTH_DECLINED);
    return stateMachine;
  }

  private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent event) {
    Message<PaymentEvent> message = MessageBuilder.withPayload(event)
        .setHeader(PAYMENT_ID_HEADER, paymentId).build();

    stateMachine.sendEvent(message);
  }

  private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
    Payment payment = repository.getById(paymentId);

    StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine(paymentId.toString());
    stateMachine.stop();
    stateMachine.getStateMachineAccessor()
        .doWithAllRegions(accessor -> {
          accessor.addStateMachineInterceptor(stateChangeInterceptor);
          accessor.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null));
        });

    stateMachine.start();

    return stateMachine;
  }
}
