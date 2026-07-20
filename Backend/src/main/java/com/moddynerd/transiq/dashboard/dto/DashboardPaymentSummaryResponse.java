package com.moddynerd.transiq.dashboard.dto;

import com.moddynerd.transiq.payment.entity.Currency;
import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.entity.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record DashboardPaymentSummaryResponse(

        UUID id,

        String paymentReference,

        Long amount,

        Currency currency,

        PaymentStatus status,

        PaymentMethodType paymentMethodType,

        String customerEmail,

        String orderId,

        Instant createdAt

) {}
