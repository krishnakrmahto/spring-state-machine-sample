package com.example.springstatemachine.domain;

/**
 * Enumerates the very states that the state machine can assume.
 */
public enum PaymentState {
    NEW,
    PRE_AUTH,
    PRE_AUTH_ERROR,
    AUTH,
    AUTH_ERROR
}
