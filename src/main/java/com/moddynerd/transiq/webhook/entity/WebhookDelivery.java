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
    private Integer attemptCount;

    @Column
    private Long durationMs;

    @Lob
    private String requestBody;

    @Lob
    private String responseBody;

    @Column
    private Instant deliveredAt;

}