package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminAlertResponse(
        String type,
        String severity,
        String message,
        UUID merchantId,
        String merchantName,
        Instant timestamp
) {}
