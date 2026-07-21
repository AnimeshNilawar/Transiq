package com.moddynerd.transiq.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String role,
        boolean enabled,
        UUID merchantId,
        String merchantName,
        Instant createdAt
) {}
