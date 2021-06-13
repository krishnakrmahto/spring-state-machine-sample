package com.example.springstatemachine.service;

import com.example.springstatemachine.domain.Payment;
import com.example.springstatemachine.domain.PaymentEvent;
import com.example.springstatemachine.domain.PaymentState;
import com.example.springstatemachine.repository.PaymentRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class PaymentServiceImplTest {

  @Autowired
  PaymentService service;

  @Autowired
  PaymentRepository repository;

  Payment payment;

  @BeforeEach
  void setUp() {
    payment = Payment.builder().amount(new BigDecimal("12.99")).build();
  }

  @Test
  @Transactional
  void preAuthorize() {
    Payment savedPayment = service.newPayment(payment);
    service.preAuthorize(savedPayment.getId());

    Payment preAuthedPayment = repository.getById(savedPayment.getId());

    System.out.println("PreAuthPayment: " + preAuthedPayment);
  }

  @RepeatedTest(10)
  @Transactional
  void authorizePayment() {
    Payment savedPayment = service.newPayment(payment);
    StateMachine<PaymentState, PaymentEvent> preAuthStateMachine = service
        .preAuthorize(savedPayment.getId());

    if (preAuthStateMachine.getState().getId().equals(PaymentState.PRE_AUTH)) {
      System.out.println("Payment is pre authorized!");

      StateMachine<PaymentState, PaymentEvent> authStateMachine = service
          .authorizePayment(savedPayment.getId());

      System.out.println("Result of auth: " + authStateMachine.getState().getId());
    } else {
      System.out.println("Payment had failed at pre-auth ...");
    }
  }
}