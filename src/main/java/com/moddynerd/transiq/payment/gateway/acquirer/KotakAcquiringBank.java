package com.moddynerd.transiq.payment.gateway.acquirer;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class KotakAcquiringBank extends AbstractAcquiringBank {

    public KotakAcquiringBank() {
        super(
                AcquiringBankCode.KOTAK,
                "Kotak Mahindra Bank",
                Set.of(CardNetwork.VISA, CardNetwork.MASTERCARD)
        );
    }

}
