package com.moddynerd.transiq.payment.gateway.payment;

import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.gateway.common.AuthorizationCodeGenerator;
import com.moddynerd.transiq.payment.gateway.model.*;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

public class WalletPaymentHandler implements PaymentHandler {

    private final SecureRandom random = new SecureRandom();
    private final AuthorizationCodeGenerator codeGenerator;

    public WalletPaymentHandler(AuthorizationCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    @Override
    public boolean supports(PaymentMethodType type) {
        return type == PaymentMethodType.WALLET;
    }

    @Override
    public AuthorizationResponse authorize(AuthorizationRequest request) {
        boolean approved = random.nextDouble() <= 0.92;

        AuthorizationMetadata metadata = new AuthorizationMetadata(
                codeGenerator.generate(),
                null,
                Instant.now(),
                Duration.ofMillis(random.nextInt(150)).toMillis()
        );

        if (approved) {
            return new AuthorizationResponse(
                    AuthorizationDecision.APPROVED,
                    AuthorizationFailureCode.NONE,
                    GatewayResponseCode.SUCCESS,
                    "Wallet payment approved",
                    metadata
            );
        }

        return new AuthorizationResponse(
                AuthorizationDecision.DECLINED,
                pickFailureCode(),
                GatewayResponseCode.DO_NOT_HONOR,
                "Wallet payment declined",
                metadata
        );
    }

    private AuthorizationFailureCode pickFailureCode() {
        double roll = random.nextDouble();
        if (roll < 0.45) return AuthorizationFailureCode.INSUFFICIENT_FUNDS;
        if (roll < 0.7) return AuthorizationFailureCode.LIMIT_EXCEEDED;
        if (roll < 0.85) return AuthorizationFailureCode.NETWORK_TIMEOUT;
        return AuthorizationFailureCode.BANK_UNAVAILABLE;
    }

}
