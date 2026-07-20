package com.moddynerd.transiq.payment.gateway.exception;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

public class PaymentNetworkNotFoundException extends RuntimeException {

    public PaymentNetworkNotFoundException(CardNetwork cardNetwork) {
        super("Payment network not found: " + cardNetwork);
    }

}
