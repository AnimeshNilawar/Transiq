package com.moddynerd.transiq.payment.dto;

import com.moddynerd.transiq.payment.entity.Currency;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(

        @NotNull
        @Min(1)
        Long amount,

        @NotNull
        Currency currency,

        @Email
        String customerEmail,

        String customerName,

        @NotBlank
        String orderId,

        String description
) {
}