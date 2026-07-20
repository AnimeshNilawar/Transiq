package com.moddynerd.transiq.payment.gateway.bank.decision;

import com.moddynerd.transiq.payment.gateway.bank.configuration.BankConfiguration;
import com.moddynerd.transiq.payment.gateway.model.*;

import java.security.SecureRandom;

public class BankDecisionEngine {

    private final SecureRandom random = new SecureRandom();

    public AuthorizationResponse decide(
            AuthorizationRequest request,
            BankConfiguration config
    ) {

        if (random.nextDouble() <= config.approvalRate()) {
            return new AuthorizationResponse(
                    AuthorizationDecision.APPROVED,
                    AuthorizationFailureCode.NONE,
                    GatewayResponseCode.SUCCESS,
                    "Payment approved",
                    null
            );
        }

        return new AuthorizationResponse(
                AuthorizationDecision.DECLINED,
                AuthorizationFailureCode.INSUFFICIENT_FUNDS,
                GatewayResponseCode.INSUFFICIENT_FUNDS,
                "Payment declined: insufficient funds",
                null
        );

    }

}
