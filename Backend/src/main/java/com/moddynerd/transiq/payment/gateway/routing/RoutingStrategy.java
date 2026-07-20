package com.moddynerd.transiq.payment.gateway.routing;

import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;

public interface RoutingStrategy {

    boolean supports(AuthorizationRequest request);

    RoutingDecision route(AuthorizationRequest request);

}
