package com.moddynerd.transiq.apikey.dto;

import com.moddynerd.transiq.apikey.entity.ApiKeyEnvironment;
import com.moddynerd.transiq.apikey.entity.ApiKeyStatus;
import com.moddynerd.transiq.apikey.entity.ApiKeyType;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String name,
        String prefix,
        ApiKeyEnvironment environment,
        ApiKeyType type,
        ApiKeyStatus status,
        Instant lastUsedAt,
        Instant createdAt
) {
}
