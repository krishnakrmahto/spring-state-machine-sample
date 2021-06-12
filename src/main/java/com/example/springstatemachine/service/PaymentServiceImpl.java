package com.example.springstatemachine.service;

import com.example.springstatemachine.domain.Payment;
import com.example.springstatemachine.domain.PaymentEvent;
import com.example.springstatemachine.domain.PaymentState;
import com.example.springstatemachine.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepository repository;
  private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

  @Transactional
  @Override
  public Payment newPayment(Payment payment) {
    payment.setState(PaymentState.NEW);
    return repository.save(payment);
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> preAuthorize(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
    StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
    return null;
  }

  private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
    Payment payment = repository.getById(paymentId);

    StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine(paymentId.toString());
    stateMachine.stop();
    stateMachine.getStateMachineAccessor()
        .doWithAllRegions(accessor -> accessor.resetStateMachine(new DefaultStateMachineContext<>(
            payment.getState(), null, null, null)));

    stateMachine.start();

    return stateMachine;
  }
}
