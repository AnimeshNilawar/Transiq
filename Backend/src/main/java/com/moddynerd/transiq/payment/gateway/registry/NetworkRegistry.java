package com.moddynerd.transiq.payment.gateway.registry;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import com.moddynerd.transiq.payment.gateway.network.PaymentNetwork;

import java.util.Collection;

public interface NetworkRegistry {

    PaymentNetwork get(CardNetwork cardNetwork);

    boolean contains(CardNetwork cardNetwork);

    Collection<PaymentNetwork> getAll();

}
