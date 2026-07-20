package com.moddynerd.transiq.payment.gateway.exception;

import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBankCode;

public class AcquiringBankNotFoundException extends RuntimeException {

    public AcquiringBankNotFoundException(AcquiringBankCode code) {
        super("Acquiring bank not found: " + code);
    }

}
