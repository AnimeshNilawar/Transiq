package com.moddynerd.transiq.payment.gateway.registry;

import com.moddynerd.transiq.payment.gateway.exception.PaymentNetworkNotFoundException;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import com.moddynerd.transiq.payment.gateway.network.PaymentNetwork;
import com.moddynerd.transiq.payment.gateway.network.implementation.MastercardNetwork;
import com.moddynerd.transiq.payment.gateway.network.implementation.RuPayNetwork;
import com.moddynerd.transiq.payment.gateway.network.implementation.VisaNetwork;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class DefaultNetworkRegistry implements NetworkRegistry {

    private final Map<CardNetwork, PaymentNetwork> networks;

    public DefaultNetworkRegistry(
            VisaNetwork visaNetwork,
            MastercardNetwork mastercardNetwork,
            RuPayNetwork ruPayNetwork
    ) {
        this.networks = Map.of(
                CardNetwork.VISA, visaNetwork,
                CardNetwork.MASTERCARD, mastercardNetwork,
                CardNetwork.RUPAY, ruPayNetwork
        );
    }

    @Override
    public PaymentNetwork get(CardNetwork cardNetwork) {
        PaymentNetwork network = networks.get(cardNetwork);
        if (network == null) {
            throw new PaymentNetworkNotFoundException(cardNetwork);
        }
        return network;
    }

    @Override
    public boolean contains(CardNetwork cardNetwork) {
        return networks.containsKey(cardNetwork);
    }

    @Override
    public Collection<PaymentNetwork> getAll() {
        return networks.values();
    }

}
