package com.moddynerd.transiq.payment.dto;

import com.moddynerd.transiq.payment.entity.Currency;
import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.entity.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(

        UUID id,

        String paymentReference,

        Long amount,

        Currency currency,

        PaymentStatus status,

        String customerEmail,

        String orderId,

        Instant createdAt,

        PaymentMethodType paymentMethodType,

        UpiPaymentDetailResponse upiDetails
) {

    public record UpiPaymentDetailResponse(
            String upiId,
            String upiTransactionReference
    ) {}
}