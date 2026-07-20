package com.moddynerd.transiq.payment.gateway.acquirer;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import lombok.Getter;

import java.util.Set;

@Getter
public abstract class AbstractAcquiringBank implements AcquiringBank {

    private final AcquiringBankCode code;
    private final String name;
    private final Set<CardNetwork> supportedNetworks;

    protected AbstractAcquiringBank(
            AcquiringBankCode code,
            String name,
            Set<CardNetwork> supportedNetworks
    ) {
        this.code = code;
        this.name = name;
        this.supportedNetworks = Set.copyOf(supportedNetworks);
    }

    @Override
    public boolean supports(CardNetwork cardNetwork) {
        return supportedNetworks.contains(cardNetwork);
    }

}
