package com.moddynerd.transiq.payment.gateway.exception;

import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBankCode;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

public class UnsupportedNetworkException extends RuntimeException {

    public UnsupportedNetworkException(
            AcquiringBankCode bankCode,
            CardNetwork network
    ) {
        super("Acquiring bank " + bankCode + " does not support network: " + network);
    }

}
