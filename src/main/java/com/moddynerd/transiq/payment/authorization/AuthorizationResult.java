package com.moddynerd.transiq.payment.authorization;

import com.moddynerd.transiq.payment.attempt.entity.FailureCode;

public record AuthorizationResult(
        AuthorizationDecision decision,
        FailureCode failureCode,
        String message
) {
}
