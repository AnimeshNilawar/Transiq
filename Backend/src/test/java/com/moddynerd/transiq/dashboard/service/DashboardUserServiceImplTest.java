package com.moddynerd.transiq.dashboard.service;

import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.auth.enums.UserRole;
import com.moddynerd.transiq.auth.exception.BadRequestException;
import com.moddynerd.transiq.auth.exception.ForbiddenException;
import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.auth.repository.MerchantUserRepository;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.merchant.enums.MerchantStatus;
import com.moddynerd.transiq.shared.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardUserServiceImplTest {

    @Mock
    private CurrentJwtUserService currentJwtUserService;
    @Mock
    private MerchantUserRepository merchantUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DashboardUserServiceImpl dashboardUserService;

    private Merchant merchant;
    private MerchantUser ownerUser;
    private MerchantUser adminUser;
    private MerchantUser developerUser;

    @BeforeEach
    void setUp() {
        merchant = Merchant.builder()
                .id(UUID.randomUUID())
                .businessName("Acme Corp")
                .businessEmail("billing@acme.com")
                .status(MerchantStatus.ACTIVE)
                .build();

        ownerUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("owner@acme.com")
                .firstName("Owner")
                .lastName("User")
                .role(UserRole.OWNER)
                .enabled(true)
                .mustChangePassword(false)
                .merchant(merchant)
                .createdAt(Instant.now())
                .build();

        developerUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("dev@acme.com")
                .firstName("Dev")
                .lastName("User")
                .role(UserRole.DEVELOPER)
                .enabled(true)
                .mustChangePassword(false)
                .merchant(merchant)
                .createdAt(Instant.now())
                .build();

        adminUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("admin@acme.com")
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.ADMIN)
                .enabled(true)
                .mustChangePassword(false)
                .merchant(merchant)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void getUsers_returnsOnlyEnabledUsers() {
        MerchantUser disabledUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("disabled@acme.com")
                .firstName("Disabled")
                .lastName("User")
                .role(UserRole.FINANCE)
                .enabled(false)
                .merchant(merchant)
                .createdAt(Instant.now())
                .build();

        when(currentJwtUserService.getCurrentMerchant()).thenReturn(merchant);
        when(merchantUserRepository.findByMerchantOrderByCreatedAtAsc(merchant))
                .thenReturn(java.util.List.of(ownerUser, developerUser, disabledUser));

        var result = dashboardUserService.getUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).email()).isEqualTo("owner@acme.com");
        assertThat(result.get(1).email()).isEqualTo("dev@acme.com");
    }

    @Test
    void inviteUser_createsUserWithHashedPassword() {
        when(currentJwtUserService.getCurrentMerchant()).thenReturn(merchant);
        when(merchantUserRepository.existsByEmail("new@acme.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        MerchantUser savedUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("new@acme.com")
                .firstName("New")
                .lastName("User")
                .role(UserRole.DEVELOPER)
                .enabled(true)
                .mustChangePassword(true)
                .merchant(merchant)
                .createdAt(Instant.now())
                .build();
        when(merchantUserRepository.save(any(MerchantUser.class))).thenReturn(savedUser);

        var result = dashboardUserService.inviteUser(
                "new@acme.com", "New", "User", UserRole.DEVELOPER
        );

        assertThat(result.email()).isEqualTo("new@acme.com");
        assertThat(result.role()).isEqualTo(UserRole.DEVELOPER);
        assertThat(result.mustChangePassword()).isTrue();
        assertThat(result.temporaryPassword()).isNotNull();
        assertThat(result.temporaryPassword()).hasSize(12);

        verify(passwordEncoder).encode(result.temporaryPassword());
        verify(merchantUserRepository).save(any(MerchantUser.class));
    }

    @Test
    void inviteUser_rejectsDuplicateEmail() {
        when(currentJwtUserService.getCurrentMerchant()).thenReturn(merchant);
        when(merchantUserRepository.existsByEmail("existing@acme.com")).thenReturn(true);

        assertThatThrownBy(() ->
                dashboardUserService.inviteUser(
                        "existing@acme.com", "Existing", "User", UserRole.DEVELOPER
                )
        ).isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void updateUserRole_success() {
        when(currentJwtUserService.getCurrentUser()).thenReturn(ownerUser);
        when(merchantUserRepository.findById(developerUser.getId()))
                .thenReturn(Optional.of(developerUser));
        when(merchantUserRepository.save(any(MerchantUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = dashboardUserService.updateUserRole(
                developerUser.getId(), UserRole.FINANCE
        );

        assertThat(result.role()).isEqualTo(UserRole.FINANCE);
    }

    @Test
    void updateUserRole_crossMerchant_returnsNotFound() {
        Merchant otherMerchant = Merchant.builder()
                .id(UUID.randomUUID())
                .businessName("Other Corp")
                .businessEmail("other@corp.com")
                .status(MerchantStatus.ACTIVE)
                .build();

        MerchantUser otherUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("other@other.com")
                .firstName("Other")
                .lastName("User")
                .role(UserRole.DEVELOPER)
                .enabled(true)
                .merchant(otherMerchant)
                .build();

        when(currentJwtUserService.getCurrentUser()).thenReturn(ownerUser);
        when(merchantUserRepository.findById(otherUser.getId()))
                .thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() ->
                dashboardUserService.updateUserRole(otherUser.getId(), UserRole.FINANCE)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateUserRole_adminCannotGrantOwner() {
        when(currentJwtUserService.getCurrentUser()).thenReturn(adminUser);
        when(merchantUserRepository.findById(developerUser.getId()))
                .thenReturn(Optional.of(developerUser));

        assertThatThrownBy(() ->
                dashboardUserService.updateUserRole(developerUser.getId(), UserRole.OWNER)
        ).isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only an OWNER can grant the OWNER role");
    }

    @Test
    void updateUserRole_lastOwnerProtection() {
        MerchantUser secondUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("second@acme.com")
                .firstName("Second")
                .lastName("User")
                .role(UserRole.ADMIN)
                .enabled(true)
                .merchant(merchant)
                .createdAt(Instant.now())
                .build();

        when(currentJwtUserService.getCurrentUser()).thenReturn(ownerUser);
        when(merchantUserRepository.findById(ownerUser.getId()))
                .thenReturn(Optional.of(ownerUser));
        when(merchantUserRepository.findByMerchantOrderByCreatedAtAsc(merchant))
                .thenReturn(java.util.List.of(ownerUser, secondUser));

        assertThatThrownBy(() ->
                dashboardUserService.updateUserRole(ownerUser.getId(), UserRole.ADMIN)
        ).isInstanceOf(BadRequestException.class)
                .hasMessageContaining("last remaining OWNER");
    }

    @Test
    void deleteUser_success() {
        when(currentJwtUserService.getCurrentUser()).thenReturn(ownerUser);
        when(merchantUserRepository.findById(developerUser.getId()))
                .thenReturn(Optional.of(developerUser));
        when(merchantUserRepository.save(any(MerchantUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        dashboardUserService.deleteUser(developerUser.getId());

        verify(merchantUserRepository).save(argThat(user -> !user.isEnabled()));
    }

    @Test
    void deleteUser_cannotDeleteSelf() {
        when(currentJwtUserService.getCurrentUser()).thenReturn(ownerUser);
        when(merchantUserRepository.findById(ownerUser.getId()))
                .thenReturn(Optional.of(ownerUser));

        assertThatThrownBy(() ->
                dashboardUserService.deleteUser(ownerUser.getId())
        ).isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot remove yourself");
    }

    @Test
    void deleteUser_lastOwnerProtection() {
        MerchantUser secondUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("second@acme.com")
                .firstName("Second")
                .lastName("User")
                .role(UserRole.ADMIN)
                .enabled(true)
                .merchant(merchant)
                .createdAt(Instant.now())
                .build();

        when(currentJwtUserService.getCurrentUser()).thenReturn(ownerUser);
        when(merchantUserRepository.findById(ownerUser.getId()))
                .thenReturn(Optional.of(ownerUser));
        when(merchantUserRepository.findByMerchantOrderByCreatedAtAsc(merchant))
                .thenReturn(java.util.List.of(ownerUser, secondUser));

        assertThatThrownBy(() ->
                dashboardUserService.deleteUser(ownerUser.getId())
        ).isInstanceOf(BadRequestException.class)
                .hasMessageContaining("last remaining OWNER");
    }

    @Test
    void deleteUser_crossMerchant_returnsNotFound() {
        Merchant otherMerchant = Merchant.builder()
                .id(UUID.randomUUID())
                .businessName("Other Corp")
                .businessEmail("other@corp.com")
                .status(MerchantStatus.ACTIVE)
                .build();

        MerchantUser otherUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("other@other.com")
                .firstName("Other")
                .lastName("User")
                .role(UserRole.DEVELOPER)
                .enabled(true)
                .merchant(otherMerchant)
                .build();

        when(currentJwtUserService.getCurrentUser()).thenReturn(ownerUser);
        when(merchantUserRepository.findById(otherUser.getId()))
                .thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() ->
                dashboardUserService.deleteUser(otherUser.getId())
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void deleteUser_alreadyDisabled_returnsNotFound() {
        MerchantUser disabledUser = MerchantUser.builder()
                .id(UUID.randomUUID())
                .email("disabled@acme.com")
                .firstName("Disabled")
                .lastName("User")
                .role(UserRole.DEVELOPER)
                .enabled(false)
                .merchant(merchant)
                .build();

        when(currentJwtUserService.getCurrentUser()).thenReturn(ownerUser);
        when(merchantUserRepository.findById(disabledUser.getId()))
                .thenReturn(Optional.of(disabledUser));

        assertThatThrownBy(() ->
                dashboardUserService.deleteUser(disabledUser.getId())
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
