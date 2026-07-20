package com.moddynerd.transiq.payment.gateway.acquirer;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;

import java.util.Set;

public class AxisAcquiringBank extends AbstractAcquiringBank {

    public AxisAcquiringBank() {
        super(
                AcquiringBankCode.AXIS,
                "Axis Bank",
                Set.of(CardNetwork.VISA, CardNetwork.MASTERCARD)
        );
    }

}
