package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminRefundResponse(
        UUID id,
        String refundReference,
        Long amount,
        String status,
        String reason,
        String paymentReference,
        UUID merchantId,
        String merchantName,
        Instant createdAt
) {}
