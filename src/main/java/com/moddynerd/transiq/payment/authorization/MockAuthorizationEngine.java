package com.moddynerd.transiq.payment.authorization;

import com.moddynerd.transiq.payment.attempt.entity.FailureCode;
import com.moddynerd.transiq.payment.entity.Payment;

import java.security.SecureRandom;

public class MockAuthorizationEngine implements AuthorizationEngine{

    private final SecureRandom random = new SecureRandom();

    @Override
    public AuthorizationResult authorize(Payment payment) {
        int value = random.nextInt(100);

        if(value < 80){
            return new AuthorizationResult(
                    AuthorizationDecision.APPROVED,
                    FailureCode.NONE,
                    "Payment Approved"
            );
        }

        FailureCode failureCode =
                switch (random.nextInt(4)) {
                    case 0 -> FailureCode.INSUFFICIENT_FUNDS;
                    case 1 -> FailureCode.BANK_DECLINED;
                    case 2 -> FailureCode.NETWORK_ERROR;
                    default -> FailureCode.TIMEOUT;
                };

        return new AuthorizationResult(
                AuthorizationDecision.DECLINED,
                failureCode,
                failureCode.name()
        );
    }
}
