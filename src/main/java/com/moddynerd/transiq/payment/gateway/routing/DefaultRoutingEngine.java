package com.moddynerd.transiq.payment.gateway.routing;

import com.moddynerd.transiq.payment.gateway.acquirer.AcquiringBankCode;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoutingEngine implements RoutingEngine {

    @Override
    public RoutingDecision route(AuthorizationRequest request) {
        return new RoutingDecision(
                AcquiringBankCode.HDFC,
                request.metadata().network(),
                request.metadata().issuerBank(),
                RoutingReason.PRIMARY_NETWORK
        );
    }

}
