package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminPaymentDetailResponse(
        UUID id,
        String paymentReference,
        Long amount,
        Long refundedAmount,
        String currency,
        String status,
        String paymentMethodType,
        UUID merchantId,
        String merchantName,
        String merchantEmail,
        String customerEmail,
        String customerName,
        String orderId,
        String description,
        String idempotencyKey,
        Instant createdAt,
        Instant expiresAt
) {}
