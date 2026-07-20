package com.moddynerd.transiq.payment.gateway.routing;

import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;

public interface RoutingEngine {

    RoutingDecision route(AuthorizationRequest request);

}
