package com.moddynerd.transiq.webhook.dto.response;

import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEventType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record WebhookDeliveryResponse(

        UUID id,

        UUID eventId,

        UUID endpointId,

        String eventReference,

        WebhookEventType eventType,

        WebhookDeliveryStatus status,

        Integer attemptCount,

        Integer httpStatus,

        String failureReason,

        Long durationMs,

        Instant deliveredAt,

        Instant createdAt,

        Instant lastAttemptAt,

        Instant nextRetryAt

) {}