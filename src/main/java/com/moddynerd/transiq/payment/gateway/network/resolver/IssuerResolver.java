package com.moddynerd.transiq.payment.gateway.network.resolver;

import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.BankCode;

public interface IssuerResolver {

    BankCode resolve(AuthorizationRequest request);

}
