package com.moddynerd.transiq.webhook.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.entity.WebhookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookEndpointRepository
        extends JpaRepository<WebhookEndpoint, UUID> {

    List<WebhookEndpoint> findAllByMerchant(
            Merchant merchant
    );

    Optional<WebhookEndpoint> findByIdAndMerchant(
            UUID id,
            Merchant merchant
    );

    List<WebhookEndpoint> findAllByStatus(WebhookStatus status);

    List<WebhookEndpoint> findAllByMerchant_IdAndStatus(
            UUID merchantId,
            WebhookStatus status
    );

}