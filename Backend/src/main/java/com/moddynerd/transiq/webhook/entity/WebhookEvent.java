package com.moddynerd.transiq.webhook.entity;

import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "webhook_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEvent extends BaseEntity {

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookEventType eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @Builder.Default
    private List<WebhookDelivery> deliveries = new ArrayList<>();

}
