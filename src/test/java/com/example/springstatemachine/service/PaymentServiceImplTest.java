package com.example.springstatemachine.service;

import com.example.springstatemachine.domain.Payment;
import com.example.springstatemachine.repository.PaymentRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}