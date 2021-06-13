package com.example.springstatemachine.config;

import com.example.springstatemachine.domain.PaymentEvent;
import com.example.springstatemachine.domain.PaymentState;
import java.util.EnumSet;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@EnableStateMachineFactory
@Configuration
@Slf4j
public class StateMachineConfigurer extends
    StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

  @Override
  public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states)
      throws Exception {
    states.withStates()
        .initial(PaymentState.NEW)
        .states(EnumSet.allOf(PaymentState.class))
        .end(PaymentState.AUTH)
        .end(PaymentState.PRE_AUTH_ERROR)
        .end(PaymentState.AUTH_ERROR);
  }

  @Override
  public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions)
      throws Exception {
    transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW)
        .event(PaymentEvent.PRE_AUTHORIZE).action(preAuthAction()).and()
        .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH)
        .event(PaymentEvent.PRE_AUTH_APPROVED).and()
        .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR)
        .event(PaymentEvent.PRE_AUTH_DECLINED);
  }

  @Override
  public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
    StateMachineListenerAdapter<PaymentState, PaymentEvent> listener = new StateMachineListenerAdapter<>() {
      @Override
      public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
        log.info(String.format("stateChanged from %s to %s", from, to));
      }
    };

    config.withConfiguration().listener(listener);
  }

  /*
  Action is how you react to a state machine event. It could be sending a msg, or the actual
  business logic, write to DB, call a web service. A lot of things you can do in here to react
  to a state machine event.
   */
  public Action<PaymentState, PaymentEvent> preAuthAction() {
    return context -> {
      log.info("PreAuth was called!");
      if (new Random().nextInt(10) < 8) {
        System.out.println("Approved");
        context.getStateMachine()
            .sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                .build());
      } else {
        System.out.println("No credit, payment declined!");
        context.getStateMachine()
            .sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                .build());
      }
    };
  }
}
