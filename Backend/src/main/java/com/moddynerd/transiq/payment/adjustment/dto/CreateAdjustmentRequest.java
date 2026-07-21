package com.moddynerd.transiq.payment.adjustment.dto;

import com.moddynerd.transiq.payment.adjustment.entity.AdjustmentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAdjustmentRequest(

        @NotNull
        @DecimalMin("1")
        Long amount,

        @NotNull
        AdjustmentType type,

        @NotBlank
        @Size(max = 500)
        String reason

) {
}
