package com.moddynerd.transiq.payment.gateway.model;

public record AuthorizationResponse(
        AuthorizationDecision decision,
        AuthorizationFailureCode failureCode,
        GatewayResponseCode responseCode,
        String message,
        AuthorizationMetadata metadata
) {}
