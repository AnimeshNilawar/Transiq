package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminPaymentResponse(
        UUID id,
        String paymentReference,
        Long amount,
        String currency,
        String status,
        UUID merchantId,
        String merchantName,
        String customerEmail,
        String customerName,
        Instant createdAt
) {}
