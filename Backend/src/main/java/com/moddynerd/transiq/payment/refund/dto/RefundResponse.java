package com.moddynerd.transiq.payment.refund.dto;

import com.moddynerd.transiq.payment.refund.entity.RefundReason;
import com.moddynerd.transiq.payment.refund.entity.RefundStatus;

import java.time.Instant;

public record RefundResponse(

        String refundReference,

        String paymentReference,

        Long amount,

        RefundStatus status,

        RefundReason reason,

        Instant createdAt

) {
}