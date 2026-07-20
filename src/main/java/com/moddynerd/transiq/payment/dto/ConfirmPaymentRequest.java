package com.moddynerd.transiq.payment.dto;

import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConfirmPaymentRequest(
        @NotNull
        String clientSecret,

        @NotNull
        PaymentMethodType paymentMethodType,

        CardNetwork cardNetwork,

        BankCode issuerBank,

        String maskedCardNumber,

        @Min(1) @Max(12)
        Integer expiryMonth,

        @Min(2024) @Max(2040)
        Integer expiryYear
) {
}
