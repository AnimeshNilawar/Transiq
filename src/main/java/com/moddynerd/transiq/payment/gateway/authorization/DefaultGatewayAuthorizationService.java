package com.moddynerd.transiq.payment.gateway.authorization;

import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;
import org.springframework.stereotype.Service;

@Service
public class DefaultGatewayAuthorizationService implements GatewayAuthorizationService {

    private final GatewayAuthorizationEngine engine;

    public DefaultGatewayAuthorizationService(GatewayAuthorizationEngine engine) {
        this.engine = engine;
    }

    @Override
    public AuthorizationResponse authorize(AuthorizationRequest request) {
        return engine.execute(request);
    }

}
