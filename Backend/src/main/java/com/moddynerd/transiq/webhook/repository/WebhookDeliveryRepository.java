package com.moddynerd.transiq.webhook.repository;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, UUID>, JpaSpecificationExecutor<WebhookDelivery> {

    List<WebhookDelivery> findByStatusAndNextRetryAtLessThanEqual(
            WebhookDeliveryStatus status,
            Instant now
    );

    Page<WebhookDelivery> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT d FROM WebhookDelivery d JOIN FETCH d.endpoint e JOIN FETCH e.merchant ORDER BY d.createdAt DESC")
    Page<WebhookDelivery> findAllWithEndpointAndMerchant(Pageable pageable);

    @Query("""
            SELECT d.endpoint.merchant.id, d.endpoint.merchant.businessName, COUNT(d), MIN(d.createdAt)
            FROM WebhookDelivery d
            WHERE d.status = 'FAILED'
            GROUP BY d.endpoint.merchant.id, d.endpoint.merchant.businessName
            ORDER BY COUNT(d) DESC
            """)
    List<Object[]> countFailedByMerchant();
}