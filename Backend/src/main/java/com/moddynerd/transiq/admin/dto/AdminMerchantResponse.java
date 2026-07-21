package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminMerchantResponse(
        UUID id,
        String businessName,
        String businessEmail,
        String status,
        Instant createdAt
) {}
