package com.moddynerd.transiq.auth.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID merchantId,
        UUID userId,
        String message
) {}