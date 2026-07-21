package com.moddynerd.transiq.payment.gateway.payment;

import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.gateway.common.AuthorizationCodeGenerator;
import com.moddynerd.transiq.payment.gateway.model.*;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

public class UpiPaymentHandler implements PaymentHandler {

    private final SecureRandom random = new SecureRandom();
    private final AuthorizationCodeGenerator codeGenerator;

    public UpiPaymentHandler(AuthorizationCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    @Override
    public boolean supports(PaymentMethodType type) {
        return type == PaymentMethodType.UPI;
    }

    @Override
    public AuthorizationResponse authorize(AuthorizationRequest request) {
        Instant start = Instant.now();

        boolean approved = random.nextDouble() <= 0.95;

        AuthorizationMetadata metadata = new AuthorizationMetadata(
                codeGenerator.generate(),
                null,
                Instant.now(),
                Duration.ofMillis(random.nextInt(200)).toMillis()
        );

        if (approved) {
            return new AuthorizationResponse(
                    AuthorizationDecision.APPROVED,
                    AuthorizationFailureCode.NONE,
                    GatewayResponseCode.SUCCESS,
                    "UPI payment approved",
                    metadata
            );
        }

        return new AuthorizationResponse(
                AuthorizationDecision.DECLINED,
                pickFailureCode(),
                GatewayResponseCode.DO_NOT_HONOR,
                "UPI payment declined",
                metadata
        );
    }

    private AuthorizationFailureCode pickFailureCode() {
        double roll = random.nextDouble();
        if (roll < 0.4) return AuthorizationFailureCode.INSUFFICIENT_FUNDS;
        if (roll < 0.7) return AuthorizationFailureCode.NETWORK_TIMEOUT;
        if (roll < 0.9) return AuthorizationFailureCode.BANK_UNAVAILABLE;
        return AuthorizationFailureCode.LIMIT_EXCEEDED;
    }

}
