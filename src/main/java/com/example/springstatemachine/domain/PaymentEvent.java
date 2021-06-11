package com.example.springstatemachine.domain;

/**
 * Enumerates the events that the state machine will be reacting to.
 */
public enum PaymentEvent {
    PRE_AUTHORIZE,
    PRE_AUTH_APPROVED,
    PRE_AUTH_DECLINED,
    AUTHORIZE,
    AUTH_APPROVED,
    AUTH_DECLINED
}
