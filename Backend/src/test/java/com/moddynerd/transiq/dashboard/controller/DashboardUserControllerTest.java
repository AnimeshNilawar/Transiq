package com.moddynerd.transiq.dashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moddynerd.transiq.auth.enums.UserRole;
import com.moddynerd.transiq.dashboard.dto.DashboardUserInfoResponse;
import com.moddynerd.transiq.dashboard.dto.InviteUserResponse;
import com.moddynerd.transiq.dashboard.service.CurrentJwtUserService;
import com.moddynerd.transiq.dashboard.service.DashboardUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardUserController.class)
class DashboardUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardUserService dashboardUserService;

    @MockitoBean
    private CurrentJwtUserService currentJwtUserService;

    private DashboardUserInfoResponse userInfo(
            UUID id, String email, UserRole role
    ) {
        return new DashboardUserInfoResponse(
                id, email, "First", "Last", role, false, Instant.now()
        );
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void getUsers_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(dashboardUserService.getUsers())
                .thenReturn(List.of(userInfo(id, "user@acme.com", UserRole.DEVELOPER)));

        mockMvc.perform(get("/api/v1/dashboard/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user@acme.com"))
                .andExpect(jsonPath("$[0].role").value("DEVELOPER"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void getUsers_developerCanView() throws Exception {
        when(dashboardUserService.getUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/dashboard/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void inviteUser_returns201() throws Exception {
        UUID id = UUID.randomUUID();
        when(dashboardUserService.inviteUser(anyString(), anyString(), anyString(), any(UserRole.class)))
                .thenReturn(new InviteUserResponse(
                        id, "new@acme.com", UserRole.DEVELOPER, "Temp123!", true, Instant.now()
                ));

        mockMvc.perform(post("/api/v1/dashboard/users/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "new@acme.com",
                                    "firstName": "New",
                                    "lastName": "User",
                                    "role": "DEVELOPER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@acme.com"))
                .andExpect(jsonPath("$.temporaryPassword").value("Temp123!"))
                .andExpect(jsonPath("$.mustChangePassword").value(true));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void inviteUser_developerGets403() throws Exception {
        mockMvc.perform(post("/api/v1/dashboard/users/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "new@acme.com",
                                    "role": "DEVELOPER"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void inviteUser_invalidRole_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/dashboard/users/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "new@acme.com",
                                    "role": "INVALID_ROLE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void updateUserRole_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(dashboardUserService.updateUserRole(id, UserRole.FINANCE))
                .thenReturn(userInfo(id, "user@acme.com", UserRole.FINANCE));

        mockMvc.perform(patch("/api/v1/dashboard/users/" + id + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": "FINANCE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("FINANCE"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void updateUserRole_developerGets403() throws Exception {
        mockMvc.perform(patch("/api/v1/dashboard/users/" + UUID.randomUUID() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": "FINANCE"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_adminCanCallButServiceChecksRole() throws Exception {
        UUID id = UUID.randomUUID();
        when(dashboardUserService.updateUserRole(id, UserRole.FINANCE))
                .thenReturn(userInfo(id, "user@acme.com", UserRole.FINANCE));

        mockMvc.perform(patch("/api/v1/dashboard/users/" + id + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role": "FINANCE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("FINANCE"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void deleteUser_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/dashboard/users/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void deleteUser_developerGets403() throws Exception {
        mockMvc.perform(delete("/api/v1/dashboard/users/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FINANCE")
    void deleteUser_financeGets403() throws Exception {
        mockMvc.perform(delete("/api/v1/dashboard/users/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }
}
