package com.moddynerd.transiq.payment.gateway.acquirer;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class IciciAcquiringBank extends AbstractAcquiringBank {

    public IciciAcquiringBank() {
        super(
                AcquiringBankCode.ICICI,
                "ICICI Bank",
                Set.of(CardNetwork.VISA, CardNetwork.MASTERCARD, CardNetwork.RUPAY)
        );
    }

}
