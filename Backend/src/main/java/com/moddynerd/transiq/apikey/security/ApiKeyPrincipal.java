package com.moddynerd.transiq.apikey.security;

import com.moddynerd.transiq.apikey.entity.ApiKeyEnvironment;
import com.moddynerd.transiq.apikey.entity.ApiKeyType;
import com.moddynerd.transiq.merchant.entity.Merchant;

import java.util.UUID;

public record ApiKeyPrincipal(
        UUID apiKeyId,
        Merchant merchant,
        ApiKeyType type,
        ApiKeyEnvironment environment
) {
}