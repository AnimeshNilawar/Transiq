package com.moddynerd.transiq.webhook.repository;

import com.moddynerd.transiq.webhook.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookEventRepository
        extends JpaRepository<WebhookEvent, UUID> {

    Optional<WebhookEvent> findByEventId(UUID eventId);

    List<WebhookEvent> findByMerchantId(UUID merchantId);

}
