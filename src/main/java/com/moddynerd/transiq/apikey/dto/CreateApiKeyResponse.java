package com.moddynerd.transiq.apikey.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateApiKeyResponse(
        UUID id,
        String apiKey,
        String prefix,
        Instant createdAt
) {
}
