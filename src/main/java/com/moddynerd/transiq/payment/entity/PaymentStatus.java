package com.moddynerd.transiq.payment.entity;

public enum PaymentStatus {
    CREATED,
    REQUIRES_PAYMENT_METHOD,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    CANCELLED,
    REFUNDED,
    EXPIRED
}