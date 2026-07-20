package com.moddynerd.transiq.payment.refund.dto;

import com.moddynerd.transiq.payment.refund.entity.RefundReason;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


public record CreateRefundRequest(

        @NotNull
        @DecimalMin("0.01")
        Long amount,

        @NotNull
        RefundReason reason

) {
}