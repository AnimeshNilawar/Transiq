package com.moddynerd.transiq.webhook.entity;

public enum WebhookEventType {

    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED,

    REFUND_SUCCEEDED,

    SETTLEMENT_COMPLETED,

    CHARGEBACK_CREATED

}