package com.moddynerd.transiq.payment.dto;

import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import jakarta.validation.constraints.NotNull;

public record ConfirmPaymentRequest(

        @NotNull
        PaymentMethodType paymentMethodType

) {
}