package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminSettlementResponse(
        UUID id,
        String settlementReference,
        Long amount,
        String currency,
        String status,
        UUID merchantId,
        String merchantName,
        Instant processedAt,
        String bankReference,
        Instant createdAt
) {}
