package com.moddynerd.transiq.apikey.repository;

import com.moddynerd.transiq.apikey.entity.ApiKeyStatus;
import com.moddynerd.transiq.apikey.entity.ApiKey;
import com.moddynerd.transiq.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    List<ApiKey> findByMerchant(Merchant merchant);

    Optional<ApiKey> findByKeyPrefix(String keyPrefix);

    List<ApiKey> findAllByMerchantOrderByCreatedAtDesc(Merchant merchant);

    Optional<ApiKey> findByIdAndMerchant(UUID id, Merchant merchant);

    Optional<ApiKey> findByKeyPrefixAndStatus(
            String keyPrefix,
            ApiKeyStatus status
    );
}
