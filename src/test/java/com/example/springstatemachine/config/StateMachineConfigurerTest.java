package com.example.springstatemachine.config;

import com.example.springstatemachine.domain.PaymentEvent;
import com.example.springstatemachine.domain.PaymentState;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

@SpringBootTest
class StateMachineConfigurerTest {

  @Autowired
  StateMachineFactory<PaymentState, PaymentEvent> factory;

  @Test
  void testNewStateMachine() {
    StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());

    stateMachine.start();

    System.out.println(stateMachine.getState());

    stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);

    System.out.println(stateMachine.getState());

    stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);

    System.out.println(stateMachine.getState());

  }
}