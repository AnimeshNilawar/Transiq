package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminApiKeyResponse(
        UUID id,
        String name,
        String keyPrefix,
        String type,
        String environment,
        String status,
        UUID merchantId,
        String merchantName,
        Instant lastUsedAt,
        Instant createdAt
) {}
