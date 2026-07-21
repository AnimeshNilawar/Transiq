package com.moddynerd.transiq.apikey;

import com.moddynerd.transiq.apikey.dto.CreateApiKeyRequest;
import com.moddynerd.transiq.apikey.dto.CreateApiKeyResponse;
import com.moddynerd.transiq.apikey.entity.ApiKey;
import com.moddynerd.transiq.apikey.entity.ApiKeyEnvironment;
import com.moddynerd.transiq.apikey.entity.ApiKeyType;
import com.moddynerd.transiq.apikey.entity.ApiKeyStatus;
import com.moddynerd.transiq.apikey.mapper.ApiKeyMapper;
import com.moddynerd.transiq.apikey.repository.ApiKeyRepository;
import com.moddynerd.transiq.apikey.service.ApiKeyServiceImpl;
import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.auth.enums.UserRole;
import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.auth.security.AuthenticatedUser;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.merchant.enums.MerchantStatus;
import com.moddynerd.transiq.merchant.repository.MerchantRepository;
import com.moddynerd.transiq.shared.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApiKeyMapper apiKeyMapper;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private ApiKeyServiceImpl apiKeyService;

    private Merchant merchant;
    private MerchantUser merchantUser;
    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    void setUp() {
        merchant = Merchant.builder()
                .id(UUID.randomUUID())
                .businessName("Acme Corp")
                .businessEmail("billing@acme.com")
                .status(MerchantStatus.ACTIVE)
                .build();

        merchantUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("admin@acme.com")
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.OWNER)
                .enabled(true)
                .merchant(merchant)
                .build();

        authenticatedUser = new AuthenticatedUser(merchantUser);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
    }

    @Test
    void createApiKey_success() {
        CreateApiKeyRequest request = new CreateApiKeyRequest(
                "Production Key", ApiKeyEnvironment.LIVE, ApiKeyType.SECRET
        );

        when(apiKeyRepository.countByMerchantAndEnvironmentAndTypeAndStatus(
                merchant, ApiKeyEnvironment.LIVE, ApiKeyType.SECRET, ApiKeyStatus.ACTIVE
        )).thenReturn(1L);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_key");
        when(apiKeyRepository.save(any(ApiKey.class)))
                .thenAnswer(invocation -> {
                    ApiKey key = invocation.getArgument(0);
                    key.setId(UUID.randomUUID());
                    return key;
                });

        CreateApiKeyResponse expectedResponse = new CreateApiKeyResponse(
                UUID.randomUUID(), "sk_live_abc123", "sk_live", Instant.now()
        );
        when(apiKeyMapper.toCreateResponse(any(ApiKey.class), anyString()))
                .thenReturn(expectedResponse);

        CreateApiKeyResponse result = apiKeyService.createApiKey(request);

        assertThat(result).isNotNull();
        assertThat(result.apiKey()).isEqualTo("sk_live_abc123");
        verify(apiKeyRepository).save(any(ApiKey.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void createApiKey_exceedsLimit_shouldFail() {
        CreateApiKeyRequest request = new CreateApiKeyRequest(
                "Extra Key", ApiKeyEnvironment.LIVE, ApiKeyType.SECRET
        );

        when(apiKeyRepository.countByMerchantAndEnvironmentAndTypeAndStatus(
                merchant, ApiKeyEnvironment.LIVE, ApiKeyType.SECRET, ApiKeyStatus.ACTIVE
        )).thenReturn(3L);

        assertThatThrownBy(() -> apiKeyService.createApiKey(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Maximum 3 active API keys allowed");
    }

    @Test
    void createApiKey_atLimitExactly_shouldFail() {
        CreateApiKeyRequest request = new CreateApiKeyRequest(
                "Extra Key", ApiKeyEnvironment.TEST, ApiKeyType.PUBLISHABLE
        );

        when(apiKeyRepository.countByMerchantAndEnvironmentAndTypeAndStatus(
                merchant, ApiKeyEnvironment.TEST, ApiKeyType.PUBLISHABLE, ApiKeyStatus.ACTIVE
        )).thenReturn(3L);

        assertThatThrownBy(() -> apiKeyService.createApiKey(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Maximum 3 active API keys allowed");
    }

    @Test
    void createApiKey_underLimit_succeeds() {
        CreateApiKeyRequest request = new CreateApiKeyRequest(
                "Dev Key", ApiKeyEnvironment.TEST, ApiKeyType.SECRET
        );

        when(apiKeyRepository.countByMerchantAndEnvironmentAndTypeAndStatus(
                merchant, ApiKeyEnvironment.TEST, ApiKeyType.SECRET, ApiKeyStatus.ACTIVE
        )).thenReturn(2L);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_key");
        when(apiKeyRepository.save(any(ApiKey.class)))
                .thenAnswer(invocation -> {
                    ApiKey key = invocation.getArgument(0);
                    key.setId(UUID.randomUUID());
                    return key;
                });

        CreateApiKeyResponse expectedResponse = new CreateApiKeyResponse(
                UUID.randomUUID(), "sk_test_xyz", "sk_test", Instant.now()
        );
        when(apiKeyMapper.toCreateResponse(any(ApiKey.class), anyString()))
                .thenReturn(expectedResponse);

        CreateApiKeyResponse result = apiKeyService.createApiKey(request);

        assertThat(result).isNotNull();
        verify(apiKeyRepository).save(any(ApiKey.class));
    }

    @Test
    void revokeApiKey_success() {
        UUID apiKeyId = UUID.randomUUID();
        ApiKey apiKey = ApiKey.builder()
                .id(apiKeyId)
                .merchant(merchant)
                .name("Production Key")
                .keyPrefix("sk_live")
                .keyHash("hashed")
                .type(ApiKeyType.SECRET)
                .environment(ApiKeyEnvironment.LIVE)
                .status(ApiKeyStatus.ACTIVE)
                .build();

        when(apiKeyRepository.findByIdAndMerchant(apiKeyId, merchant))
                .thenReturn(Optional.of(apiKey));
        when(apiKeyRepository.save(any(ApiKey.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        apiKeyService.revokeApiKey(apiKeyId);

        verify(apiKeyRepository).save(argThat(key -> key.getStatus() == ApiKeyStatus.REVOKED));
    }

    @Test
    void revokeApiKey_notFound_shouldFail() {
        UUID nonExistentId = UUID.randomUUID();

        when(apiKeyRepository.findByIdAndMerchant(nonExistentId, merchant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> apiKeyService.revokeApiKey(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("API Key not found");
    }

    @Test
    void rotateApiKey_onActiveKey() {
        UUID apiKeyId = UUID.randomUUID();
        ApiKey oldKey = ApiKey.builder()
                .id(apiKeyId)
                .merchant(merchant)
                .name("Production Key")
                .keyPrefix("sk_live_old")
                .keyHash("hashed_old")
                .type(ApiKeyType.SECRET)
                .environment(ApiKeyEnvironment.LIVE)
                .status(ApiKeyStatus.ACTIVE)
                .build();

        when(apiKeyRepository.findByIdAndMerchant(apiKeyId, merchant))
                .thenReturn(Optional.of(oldKey));
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_new");
        when(apiKeyRepository.save(any(ApiKey.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateApiKeyResponse expectedResponse = new CreateApiKeyResponse(
                UUID.randomUUID(), "sk_live_new_key", "sk_live", Instant.now()
        );
        when(apiKeyMapper.toCreateResponse(any(ApiKey.class), anyString()))
                .thenReturn(expectedResponse);

        CreateApiKeyResponse result = apiKeyService.rotateApiKey(apiKeyId);

        assertThat(result).isNotNull();
        assertThat(result.apiKey()).isEqualTo("sk_live_new_key");
        verify(apiKeyRepository, times(2)).save(any(ApiKey.class));
    }

    @Test
    void rotateApiKey_oldKeyIsRevoked() {
        UUID apiKeyId = UUID.randomUUID();
        ApiKey oldKey = ApiKey.builder()
                .id(apiKeyId)
                .merchant(merchant)
                .name("Production Key")
                .keyPrefix("sk_live_old")
                .keyHash("hashed_old")
                .type(ApiKeyType.SECRET)
                .environment(ApiKeyEnvironment.LIVE)
                .status(ApiKeyStatus.ACTIVE)
                .build();

        when(apiKeyRepository.findByIdAndMerchant(apiKeyId, merchant))
                .thenReturn(Optional.of(oldKey));
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_new");
        when(apiKeyRepository.save(any(ApiKey.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateApiKeyResponse expectedResponse = new CreateApiKeyResponse(
                UUID.randomUUID(), "sk_live_new", "sk_live", Instant.now()
        );
        when(apiKeyMapper.toCreateResponse(any(ApiKey.class), anyString()))
                .thenReturn(expectedResponse);

        apiKeyService.rotateApiKey(apiKeyId);

        verify(apiKeyRepository).save(argThat(key -> key.getId().equals(apiKeyId) && key.getStatus() == ApiKeyStatus.REVOKED));
    }

    @Test
    void rotateApiKey_onRevokedKey_shouldFail() {
        UUID apiKeyId = UUID.randomUUID();
        ApiKey revokedKey = ApiKey.builder()
                .id(apiKeyId)
                .merchant(merchant)
                .name("Old Key")
                .keyPrefix("sk_live_revoked")
                .keyHash("hashed")
                .type(ApiKeyType.SECRET)
                .environment(ApiKeyEnvironment.LIVE)
                .status(ApiKeyStatus.REVOKED)
                .build();

        when(apiKeyRepository.findByIdAndMerchant(apiKeyId, merchant))
                .thenReturn(Optional.of(revokedKey));

        assertThatThrownBy(() -> apiKeyService.rotateApiKey(apiKeyId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Only active API keys can be rotated");
    }

    @Test
    void rotateApiKey_notFound_shouldFail() {
        UUID nonExistentId = UUID.randomUUID();

        when(apiKeyRepository.findByIdAndMerchant(nonExistentId, merchant))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> apiKeyService.rotateApiKey(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("API Key not found");
    }
}
