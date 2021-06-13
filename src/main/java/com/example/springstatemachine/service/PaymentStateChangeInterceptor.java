package com.example.springstatemachine.service;

import com.example.springstatemachine.domain.Payment;
import com.example.springstatemachine.domain.PaymentEvent;
import com.example.springstatemachine.domain.PaymentState;
import com.example.springstatemachine.repository.PaymentRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

  private final PaymentRepository repository;

  @Override
  public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
      Transition<PaymentState, PaymentEvent> transition,
      StateMachine<PaymentState, PaymentEvent> stateMachine,
      StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
    Optional.ofNullable(message)
        .ifPresent(msg -> Optional.ofNullable(msg.getHeaders().get(PaymentServiceImpl.PAYMENT_ID_HEADER))
            .map(Object::toString)
            .map(Long::parseLong)
            .ifPresent(paymentId -> {
              Payment payment = repository.getById(paymentId);
              payment.setState(state.getId());
            }));
  }
}
