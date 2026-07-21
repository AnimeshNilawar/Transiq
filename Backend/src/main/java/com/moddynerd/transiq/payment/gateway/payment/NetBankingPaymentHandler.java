package com.moddynerd.transiq.payment.gateway.payment;

import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.gateway.common.AuthorizationCodeGenerator;
import com.moddynerd.transiq.payment.gateway.model.*;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class NetBankingPaymentHandler implements PaymentHandler {

    private final SecureRandom random = new SecureRandom();
    private final AuthorizationCodeGenerator codeGenerator;

    private static final Map<String, Double> BANK_APPROVAL_RATES = Map.of(
            "HDFC", 0.93,
            "ICICI", 0.91,
            "AXIS", 0.88,
            "SBI", 0.85,
            "KOTAK", 0.90,
            "DEFAULT", 0.90
    );

    public NetBankingPaymentHandler(AuthorizationCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    @Override
    public boolean supports(PaymentMethodType type) {
        return type == PaymentMethodType.NET_BANKING;
    }

    @Override
    public AuthorizationResponse authorize(AuthorizationRequest request) {
        double approvalRate = BANK_APPROVAL_RATES.getOrDefault("DEFAULT", 0.90);

        boolean approved = random.nextDouble() <= approvalRate;

        AuthorizationMetadata metadata = new AuthorizationMetadata(
                codeGenerator.generate(),
                null,
                Instant.now(),
                Duration.ofMillis(random.nextInt(500)).toMillis()
        );

        if (approved) {
            return new AuthorizationResponse(
                    AuthorizationDecision.APPROVED,
                    AuthorizationFailureCode.NONE,
                    GatewayResponseCode.SUCCESS,
                    "Net banking payment approved",
                    metadata
            );
        }

        return new AuthorizationResponse(
                AuthorizationDecision.DECLINED,
                pickFailureCode(),
                GatewayResponseCode.DO_NOT_HONOR,
                "Net banking payment declined",
                metadata
        );
    }

    private AuthorizationFailureCode pickFailureCode() {
        double roll = random.nextDouble();
        if (roll < 0.35) return AuthorizationFailureCode.BANK_UNAVAILABLE;
        if (roll < 0.6) return AuthorizationFailureCode.INSUFFICIENT_FUNDS;
        if (roll < 0.8) return AuthorizationFailureCode.NETWORK_TIMEOUT;
        return AuthorizationFailureCode.LIMIT_EXCEEDED;
    }

}
