package com.moddynerd.transiq.payment.gateway.network.resolver;

import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import org.springframework.stereotype.Component;

@Component
public class DefaultIssuerResolver implements IssuerResolver {

    @Override
    public BankCode resolve(AuthorizationRequest request) {
        return request.metadata().issuerBank();
    }

}
