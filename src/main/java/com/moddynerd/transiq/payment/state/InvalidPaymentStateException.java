package com.moddynerd.transiq.payment.state;

import com.moddynerd.transiq.shared.exception.ConflictException;

public class InvalidPaymentStateException extends ConflictException {

    public InvalidPaymentStateException(String message) {
        super(message);
    }
}