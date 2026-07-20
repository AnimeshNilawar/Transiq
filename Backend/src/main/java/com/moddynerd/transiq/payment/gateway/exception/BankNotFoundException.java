package com.moddynerd.transiq.payment.gateway.exception;

import com.moddynerd.transiq.payment.gateway.model.BankCode;

public class BankNotFoundException extends RuntimeException {

    public BankNotFoundException(BankCode bankCode) {
        super("Bank not found: " + bankCode);
    }

}
