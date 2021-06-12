package com.example.springstatemachine.service;

import com.example.springstatemachine.domain.Payment;
import com.example.springstatemachine.domain.PaymentEvent;
import com.example.springstatemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

  Payment newPayment(Payment payment);

  StateMachine<PaymentState, PaymentEvent> preAuthorize(Long paymentId);

  StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);

  StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
