package com.moddynerd.transiq.webhook.entity;

import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "webhook_deliveries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDelivery extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private WebhookEndpoint endpoint;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String eventReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookDeliveryStatus status;

    @Column
    private Integer httpStatus;

    @Column
    private Long durationMs;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column
    private Instant deliveredAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 1;

    @Column
    private Instant lastAttemptAt;

    @Column
    private Instant nextRetryAt;

    @Column(length = 1024)
    private String failureReason;

}