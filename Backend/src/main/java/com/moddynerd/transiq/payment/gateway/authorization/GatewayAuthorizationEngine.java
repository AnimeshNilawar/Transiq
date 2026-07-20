package com.moddynerd.transiq.payment.gateway.authorization;

import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBank;
import com.moddynerd.transiq.payment.gateway.exception.UnsupportedNetworkException;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;
import com.moddynerd.transiq.payment.gateway.network.PaymentNetwork;
import com.moddynerd.transiq.payment.gateway.registry.AcquiringRegistry;
import com.moddynerd.transiq.payment.gateway.registry.NetworkRegistry;
import com.moddynerd.transiq.payment.gateway.routing.RoutingDecision;
import com.moddynerd.transiq.payment.gateway.routing.RoutingEngine;
import org.springframework.stereotype.Component;

@Component
public class GatewayAuthorizationEngine {

    private final RoutingEngine routingEngine;
    private final AcquiringRegistry acquiringRegistry;
    private final NetworkRegistry networkRegistry;

    public GatewayAuthorizationEngine(
            RoutingEngine routingEngine,
            AcquiringRegistry acquiringRegistry,
            NetworkRegistry networkRegistry
    ) {
        this.routingEngine = routingEngine;
        this.acquiringRegistry = acquiringRegistry;
        this.networkRegistry = networkRegistry;
    }

    public AuthorizationResponse execute(AuthorizationRequest request) {
        RoutingDecision decision = routingEngine.route(request);

        AcquiringBank acquirer = acquiringRegistry.get(decision.acquirer());

        if (!acquirer.supports(decision.network())) {
            throw new UnsupportedNetworkException(
                    decision.acquirer(),
                    decision.network()
            );
        }

        PaymentNetwork network = networkRegistry.get(decision.network());
        return network.authorize(request);
    }

}
