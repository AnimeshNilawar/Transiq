package com.moddynerd.transiq.shared.security;

import com.moddynerd.transiq.apikey.security.ApiKeyPrincipal;

public interface CurrentApiKeyService {

    ApiKeyPrincipal getCurrentPrincipal();

}