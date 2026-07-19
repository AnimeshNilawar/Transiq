package com.moddynerd.transiq.webhook.repository;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, UUID>, JpaSpecificationExecutor<WebhookDelivery> {

    List<WebhookDelivery> findByStatusAndNextRetryAtLessThanEqual(
            WebhookDeliveryStatus status,
            Instant now
    );

    Page<WebhookDelivery> findAllByOrderByCreatedAtDesc(Pageable pageable);
}