package com.moddynerd.transiq.payment.gateway.model;

import java.time.Instant;

public record AuthorizationMetadata(
        String authorizationCode,
        BankCode issuerBank,
        Instant processedAt,
        long latencyMs
) {}
