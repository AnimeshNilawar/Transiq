package com.moddynerd.transiq.payment.gateway.model;

public enum GatewayResponseCode {

    SUCCESS,
    DO_NOT_HONOR,
    INSUFFICIENT_FUNDS,
    EXPIRED_CARD,
    INVALID_CVV,
    ISSUER_UNAVAILABLE,
    SYSTEM_ERROR,
    UNKNOWN

}
