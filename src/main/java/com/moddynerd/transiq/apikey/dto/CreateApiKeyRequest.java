package com.moddynerd.transiq.apikey.dto;

import com.moddynerd.transiq.apikey.entity.ApiKeyEnvironment;
import com.moddynerd.transiq.apikey.entity.ApiKeyType;

public record CreateApiKeyRequest(
        String name,
        ApiKeyEnvironment environment,
        ApiKeyType type
) {
}
