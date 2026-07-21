package com.moddynerd.transiq.payment.chargeback.dto;

import com.moddynerd.transiq.payment.chargeback.entity.ChargebackStatus;

import java.time.Instant;

public record ChargebackResponse(

        String chargebackReference,

        String paymentReference,

        Long amount,

        ChargebackStatus status,

        String reason,

        String evidence,

        Instant createdAt

) {
}
