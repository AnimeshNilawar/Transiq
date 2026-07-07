package com.moddynerd.transiq.payment.dto;

import com.moddynerd.transiq.payment.entity.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record CreatePaymentResponse(

        UUID id,

        String paymentReference,

        String clientSecret,

        PaymentStatus status,

        Instant createdAt
) {
}