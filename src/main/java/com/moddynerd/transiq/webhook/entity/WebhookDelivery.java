package com.moddynerd.transiq.webhook.entity;

import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private WebhookEvent event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookDeliveryStatus status;

    @Column
    private Integer httpStatus;

    @Column
    private Long durationMs;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column
    private Instant deliveredAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    @Column
    private Instant lastAttemptAt;

    @Column
    private Instant nextRetryAt;

    @Column(length = 1024)
    private String failureReason;

}
