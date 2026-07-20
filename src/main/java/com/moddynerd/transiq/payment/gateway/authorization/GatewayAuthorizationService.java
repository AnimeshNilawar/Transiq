package com.moddynerd.transiq.payment.gateway.authorization;

import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;

public interface GatewayAuthorizationService {

    AuthorizationResponse authorize(AuthorizationRequest request);

}
