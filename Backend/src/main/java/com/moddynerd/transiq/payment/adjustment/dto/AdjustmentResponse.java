package com.moddynerd.transiq.payment.adjustment.dto;

import com.moddynerd.transiq.payment.adjustment.entity.AdjustmentType;

import java.time.Instant;

public record AdjustmentResponse(

        String adjustmentReference,

        Long amount,

        AdjustmentType type,

        String reason,

        String createdBy,

        Instant createdAt

) {
}
