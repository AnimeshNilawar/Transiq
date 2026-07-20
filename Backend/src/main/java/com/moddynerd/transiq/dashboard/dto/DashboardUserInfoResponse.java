package com.moddynerd.transiq.dashboard.dto;

import com.moddynerd.transiq.auth.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

public record DashboardUserInfoResponse(

        UUID id,

        String email,

        String firstName,

        String lastName,

        UserRole role,

        boolean mustChangePassword,

        Instant createdAt

) {}
