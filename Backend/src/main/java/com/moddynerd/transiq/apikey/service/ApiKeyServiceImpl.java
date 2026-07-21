package com.moddynerd.transiq.apikey.service;

import com.moddynerd.transiq.apikey.dto.ApiKeyResponse;
import com.moddynerd.transiq.apikey.dto.CreateApiKeyRequest;
import com.moddynerd.transiq.apikey.dto.CreateApiKeyResponse;
import com.moddynerd.transiq.apikey.entity.ApiKeyStatus;
import com.moddynerd.transiq.apikey.entity.ApiKey;
import com.moddynerd.transiq.apikey.mapper.ApiKeyMapper;
import com.moddynerd.transiq.apikey.repository.ApiKeyRepository;
import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.auth.security.AuthenticatedUser;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.merchant.repository.MerchantRepository;
import com.moddynerd.transiq.shared.exception.ConflictException;
import com.moddynerd.transiq.shared.util.ApiKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService{

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeyMapper apiKeyMapper;

    @Override
    public CreateApiKeyResponse createApiKey(CreateApiKeyRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        MerchantUser currentUser = authenticatedUser.getUser();

        Merchant merchant = currentUser.getMerchant();

        long activeCount = apiKeyRepository.countByMerchantAndEnvironmentAndTypeAndStatus(
                merchant,
                request.environment(),
                request.type(),
                ApiKeyStatus.ACTIVE
        );
        if (activeCount >= 3) {
            throw new ConflictException("Maximum 3 active API keys allowed per environment and type");
        }

        String plainApiKey = ApiKeyGenerator.generate(
                request.type(),
                request.environment()
        );

        String hash = passwordEncoder.encode(plainApiKey);

        ApiKey apikey = ApiKey.builder()
                .merchant(merchant)
                .name(request.name())
                .keyPrefix(ApiKeyGenerator.getPrefix(plainApiKey))
                .keyHash(hash)
                .type(request.type())
                .environment(request.environment())
                .status(ApiKeyStatus.ACTIVE)
                .build();

        apiKeyRepository.save(apikey);

        return apiKeyMapper.toCreateResponse(
                apikey,
                plainApiKey
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyResponse> getApiKeys() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        Merchant merchant = authenticatedUser.getUser().getMerchant();

        return apiKeyRepository
                .findAllByMerchantOrderByCreatedAtDesc(merchant)
                .stream()
                .map(apiKeyMapper::toResponse)
                .toList();
    }

    @Override
    public void revokeApiKey(UUID apiKeyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        Merchant merchant = authenticatedUser.getUser().getMerchant();

        ApiKey apikey =apiKeyRepository
                .findByIdAndMerchant(apiKeyId, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("API Key not found"));

        apikey.setStatus(ApiKeyStatus.REVOKED);

        apiKeyRepository.save(apikey);
    }

    @Override
    public CreateApiKeyResponse rotateApiKey(UUID apiKeyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        Merchant merchant = authenticatedUser.getUser().getMerchant();

        ApiKey oldKey =apiKeyRepository
                .findByIdAndMerchant(apiKeyId, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("API Key not found"));

        if (oldKey.getStatus() != ApiKeyStatus.ACTIVE) {
            throw new ConflictException(
                    "Only active API keys can be rotated"
            );
        }

        oldKey.setStatus(ApiKeyStatus.REVOKED);

        String plainApiKey = ApiKeyGenerator.generate(
                oldKey.getType(),
                oldKey.getEnvironment()
        );

        String hash = passwordEncoder.encode(plainApiKey);

        ApiKey newKey = ApiKey.builder()
                .merchant(merchant)
                .name(oldKey.getName())
                .keyPrefix(ApiKeyGenerator.getPrefix(plainApiKey))
                .keyHash(hash)
                .type(oldKey.getType())
                .environment(oldKey.getEnvironment())
                .status(ApiKeyStatus.ACTIVE)
                .expiresAt(oldKey.getExpiresAt())
                .build();

        apiKeyRepository.save(oldKey);
        apiKeyRepository.save(newKey);

        return apiKeyMapper.toCreateResponse(
                newKey,
                plainApiKey
        );
    }
}
