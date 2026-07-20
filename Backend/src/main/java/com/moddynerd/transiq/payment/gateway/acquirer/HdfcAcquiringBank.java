package com.moddynerd.transiq.payment.gateway.acquirer;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class HdfcAcquiringBank extends AbstractAcquiringBank {

    public HdfcAcquiringBank() {
        super(
                AcquiringBankCode.HDFC,
                "HDFC Bank",
                Set.of(CardNetwork.VISA, CardNetwork.MASTERCARD, CardNetwork.RUPAY)
        );
    }

}
