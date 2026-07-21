package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminMerchantDetailResponse(
        UUID id,
        String businessName,
        String businessEmail,
        String status,
        int userCount,
        int apiKeyCount,
        long totalPaymentVolume,
        long totalPaymentCount,
        Instant createdAt
) {}
