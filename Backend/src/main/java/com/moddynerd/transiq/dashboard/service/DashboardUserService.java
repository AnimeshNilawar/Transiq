package com.moddynerd.transiq.dashboard.service;

import com.moddynerd.transiq.auth.enums.UserRole;
import com.moddynerd.transiq.dashboard.dto.DashboardUserInfoResponse;
import com.moddynerd.transiq.dashboard.dto.InviteUserResponse;

import java.util.List;
import java.util.UUID;

public interface DashboardUserService {

    List<DashboardUserInfoResponse> getUsers();

    InviteUserResponse inviteUser(
            String email,
            String firstName,
            String lastName,
            UserRole role
    );

    DashboardUserInfoResponse updateUserRole(
            UUID userId,
            UserRole newRole
    );

    void deleteUser(UUID userId);
}
