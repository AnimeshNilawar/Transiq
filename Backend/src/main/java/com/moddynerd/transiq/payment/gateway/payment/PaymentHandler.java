package com.moddynerd.transiq.payment.gateway.payment;

import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;

public interface PaymentHandler {

    boolean supports(PaymentMethodType type);

    AuthorizationResponse authorize(AuthorizationRequest request);

}
