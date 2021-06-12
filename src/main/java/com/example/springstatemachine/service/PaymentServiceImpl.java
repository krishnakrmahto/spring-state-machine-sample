package com.example.springstatemachine.service;

import com.example.springstatemachine.domain.Payment;
import com.example.springstatemachine.domain.PaymentEvent;
import com.example.springstatemachine.domain.PaymentState;
import com.example.springstatemachine.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
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
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
    return null;
  }

  @Override
  public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
    return null;
  }
}
