package com.moddynerd.transiq.shared.security;

import com.moddynerd.transiq.apikey.security.ApiKeyPrincipal;
import com.moddynerd.transiq.shared.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentApiKeyServiceImpl
        implements CurrentApiKeyService {

    @Override
    public ApiKeyPrincipal getCurrentPrincipal() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof ApiKeyPrincipal principal)) {

            throw new UnauthorizedException("API Key authentication required");
        }

        return principal;
    }
}