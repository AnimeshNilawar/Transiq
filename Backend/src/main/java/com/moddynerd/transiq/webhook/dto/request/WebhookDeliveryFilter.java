package com.moddynerd.transiq.webhook.dto.request;

import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEventType;

import java.time.Instant;
import java.util.UUID;

public record WebhookDeliveryFilter(

        WebhookDeliveryStatus status,

        WebhookEventType eventType,

        UUID endpointId,

        UUID eventId,

        Instant from,

        Instant to

) {}