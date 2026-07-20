package com.moddynerd.transiq.dashboard.dto;

import com.moddynerd.transiq.auth.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteUserRequest(

        @NotBlank String email,

        String firstName,

        String lastName,

        @NotNull UserRole role

) {}
