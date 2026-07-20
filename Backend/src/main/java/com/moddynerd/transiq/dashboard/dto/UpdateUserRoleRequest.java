package com.moddynerd.transiq.dashboard.dto;

import com.moddynerd.transiq.auth.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(

        @NotNull UserRole role

) {}
