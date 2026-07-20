package com.moddynerd.transiq.dashboard.dto;

import com.moddynerd.transiq.auth.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record InviteUserResponse(

        UUID id,

        String email,

        UserRole role,

        String temporaryPassword,

        boolean mustChangePassword,

        Instant createdAt

) {}
