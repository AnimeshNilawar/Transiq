package com.moddynerd.transiq.apikey.service;

import com.moddynerd.transiq.apikey.entity.ApiKey;
import com.moddynerd.transiq.apikey.entity.ApiKeyStatus;
import com.moddynerd.transiq.apikey.repository.ApiKeyRepository;
import com.moddynerd.transiq.apikey.security.ApiKeyPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ApiKeyAuthenticationServiceImpl
        implements ApiKeyAuthenticationService {

    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ApiKeyPrincipal authenticate(String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new BadCredentialsException("API Key is missing");
        }

        if (apiKey.length() < 16) {
            throw new BadCredentialsException("Invalid API Key");
        }

        String prefix = apiKey.substring(0, 16);

        ApiKey storedKey = apiKeyRepository
                .findByKeyPrefixAndStatus(prefix, ApiKeyStatus.ACTIVE)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid API Key"));

        if (!passwordEncoder.matches(apiKey, storedKey.getKeyHash())) {
            throw new BadCredentialsException("Invalid API Key");
        }

        storedKey.setLastUsedAt(Instant.now());
        apiKeyRepository.save(storedKey);

        storedKey.getMerchant().getBusinessName();

        return new ApiKeyPrincipal(
                storedKey.getId(),
                storedKey.getMerchant(),
                storedKey.getType(),
                storedKey.getEnvironment()
        );
    }
}
