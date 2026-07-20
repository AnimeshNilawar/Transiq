package com.moddynerd.transiq.payment.gateway.bank.simulator;

import com.moddynerd.transiq.payment.gateway.bank.configuration.BankConfiguration;
import com.moddynerd.transiq.payment.gateway.bank.decision.BankDecisionEngine;
import com.moddynerd.transiq.payment.gateway.common.AuthorizationCodeGenerator;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationMetadata;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationRequest;
import com.moddynerd.transiq.payment.gateway.model.AuthorizationResponse;

import java.time.Duration;
import java.time.Instant;

public class AuthorizationSimulator {

    private final BankDecisionEngine decisionEngine;
    private final AuthorizationCodeGenerator codeGenerator;

    public AuthorizationSimulator(
            BankDecisionEngine decisionEngine,
            AuthorizationCodeGenerator codeGenerator
    ) {
        this.decisionEngine = decisionEngine;
        this.codeGenerator = codeGenerator;
    }

    public AuthorizationResponse authorize(
            AuthorizationRequest request,
            BankConfiguration config
    ) {

        AuthorizationResponse partial =
                decisionEngine.decide(request, config);

        AuthorizationMetadata metadata = new AuthorizationMetadata(
                codeGenerator.generate(),
                config.bankCode(),
                Instant.now(),
                Duration.ZERO.toMillis()
        );

        return new AuthorizationResponse(
                partial.decision(),
                partial.failureCode(),
                partial.responseCode(),
                partial.message(),
                metadata
        );

    }

}
