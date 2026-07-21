package com.moddynerd.transiq.payment.chargeback.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateChargebackRequest(

        @NotBlank
        String paymentReference,

        @NotNull
        @DecimalMin("1")
        Long amount,

        @NotBlank
        @Size(max = 500)
        String reason,

        @Size(max = 10000)
        String evidence

) {
}
