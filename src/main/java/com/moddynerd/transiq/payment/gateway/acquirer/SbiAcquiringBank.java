package com.moddynerd.transiq.payment.gateway.acquirer;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class SbiAcquiringBank extends AbstractAcquiringBank {

    public SbiAcquiringBank() {
        super(
                AcquiringBankCode.SBI,
                "State Bank of India",
                Set.of(CardNetwork.RUPAY, CardNetwork.VISA)
        );
    }

}
