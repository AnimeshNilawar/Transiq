package com.moddynerd.transiq.payment.attempt.entity;

public enum FailureCode {
    NONE,
    NETWORK_ERROR,
    BANK_DECLINED,
    INSUFFICIENT_FUNDS,
    INVALID_PAYMENT_METHOD,
    TIMEOUT,
    UNKNOWN
}
