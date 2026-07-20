package com.moddynerd.transiq.payment.gateway.acquirer;

import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import com.moddynerd.transiq.payment.gateway.model.GatewayComponent;

import java.util.Set;

public interface AcquiringBank extends GatewayComponent {

    AcquiringBankCode getCode();

    String getName();

    Set<CardNetwork> getSupportedNetworks();

    boolean supports(CardNetwork cardNetwork);

}
