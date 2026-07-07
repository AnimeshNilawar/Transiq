package com.moddynerd.transiq.apikey.service;

import com.moddynerd.transiq.apikey.dto.ApiKeyResponse;
import com.moddynerd.transiq.apikey.dto.CreateApiKeyRequest;
import com.moddynerd.transiq.apikey.dto.CreateApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    CreateApiKeyResponse createApiKey(CreateApiKeyRequest request);
    List<ApiKeyResponse> getApiKeys();
    void revokeApiKey(UUID apiKeyId);
    CreateApiKeyResponse rotateApiKey(UUID apiKeyId);
}
