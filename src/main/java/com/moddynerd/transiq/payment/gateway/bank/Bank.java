package com.moddynerd.transiq.payment.gateway.bank;

import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.GatewayComponent;

public interface Bank extends GatewayComponent {

    BankCode code();

    AuthorizationResponse authorize(AuthorizationRequest request);

}
