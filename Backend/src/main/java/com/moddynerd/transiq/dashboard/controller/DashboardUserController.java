package com.moddynerd.transiq.dashboard.controller;

import com.moddynerd.transiq.dashboard.dto.DashboardUserInfoResponse;
import com.moddynerd.transiq.dashboard.dto.InviteUserRequest;
import com.moddynerd.transiq.dashboard.dto.InviteUserResponse;
import com.moddynerd.transiq.dashboard.dto.UpdateUserRoleRequest;
import com.moddynerd.transiq.dashboard.service.DashboardUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard/users")
@RequiredArgsConstructor
public class DashboardUserController {

    private final DashboardUserService dashboardUserService;

    @GetMapping
    public ResponseEntity<List<DashboardUserInfoResponse>> getUsers() {
        return ResponseEntity.ok(
                dashboardUserService.getUsers()
        );
    }

    @PostMapping("/invite")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<InviteUserResponse> inviteUser(
            @Valid @RequestBody InviteUserRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        dashboardUserService.inviteUser(
                                request.email(),
                                request.firstName(),
                                request.lastName(),
                                request.role()
                        )
                );
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<DashboardUserInfoResponse> updateUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        return ResponseEntity.ok(
                dashboardUserService.updateUserRole(id, request.role())
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        dashboardUserService.deleteUser(id);
    }
}
