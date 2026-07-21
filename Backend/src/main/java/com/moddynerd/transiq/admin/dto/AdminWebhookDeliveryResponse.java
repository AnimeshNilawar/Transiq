package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminWebhookDeliveryResponse(
        UUID id,
        String status,
        Integer httpStatus,
        String eventType,
        String eventReference,
        String endpointUrl,
        UUID merchantId,
        String merchantName,
        Integer attemptCount,
        String failureReason,
        Instant createdAt,
        Instant lastAttemptAt,
        Instant nextRetryAt
) {}
