package com.moddynerd.transiq.apikey.service;

import com.moddynerd.transiq.apikey.security.ApiKeyPrincipal;

public interface ApiKeyAuthenticationService {

    ApiKeyPrincipal authenticate(String apiKey);

}