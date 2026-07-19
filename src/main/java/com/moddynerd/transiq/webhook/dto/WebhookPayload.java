package com.moddynerd.transiq.webhook.dto;

import java.time.Instant;
import java.util.UUID;

public record WebhookPayload(

        UUID id,

        String type,

        String reference,

        Instant occurredAt,

        Object data

) {
}