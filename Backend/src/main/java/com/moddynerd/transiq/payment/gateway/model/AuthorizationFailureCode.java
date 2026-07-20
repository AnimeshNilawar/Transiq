package com.moddynerd.transiq.payment.gateway.model;

public enum AuthorizationFailureCode {

    NONE,
    INSUFFICIENT_FUNDS,
    CARD_EXPIRED,
    CARD_BLOCKED,
    CVV_INVALID,
    NETWORK_TIMEOUT,
    BANK_UNAVAILABLE,
    LIMIT_EXCEEDED,
    SUSPECTED_FRAUD,
    UNKNOWN

}
