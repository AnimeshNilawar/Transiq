package com.moddynerd.transiq.payment.gateway.network;

import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import com.moddynerd.transiq.payment.gateway.model.GatewayComponent;

public interface PaymentNetwork extends GatewayComponent {

    CardNetwork network();

    AuthorizationResponse authorize(AuthorizationRequest request);

}
