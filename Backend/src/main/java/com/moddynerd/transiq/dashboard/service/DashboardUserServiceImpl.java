package com.moddynerd.transiq.dashboard.service;

import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.auth.enums.UserRole;
import com.moddynerd.transiq.auth.exception.BadRequestException;
import com.moddynerd.transiq.auth.exception.ForbiddenException;
import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.auth.repository.MerchantUserRepository;
import com.moddynerd.transiq.dashboard.dto.DashboardUserInfoResponse;
import com.moddynerd.transiq.dashboard.dto.InviteUserResponse;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.shared.exception.ConflictException;
import com.moddynerd.transiq.shared.util.TempPasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardUserServiceImpl implements DashboardUserService {

    private final CurrentJwtUserService currentJwtUserService;
    private final MerchantUserRepository merchantUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<DashboardUserInfoResponse> getUsers() {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        return merchantUserRepository
                .findByMerchantOrderByCreatedAtAsc(merchant)
                .stream()
                .filter(MerchantUser::isEnabled)
                .map(this::toUserInfoResponse)
                .toList();
    }

    @Override
    public InviteUserResponse inviteUser(
            String email,
            String firstName,
            String lastName,
            UserRole role
    ) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        if (merchantUserRepository.existsByEmail(email)) {
            throw new ConflictException(
                    "A user with this email already exists"
            );
        }

        String tempPassword = TempPasswordGenerator.generate();
        String hashedPassword = passwordEncoder.encode(tempPassword);

        MerchantUser user = MerchantUser.builder()
                .email(email)
                .firstName(firstName != null ? firstName : "")
                .lastName(lastName != null ? lastName : "")
                .password(hashedPassword)
                .role(role)
                .enabled(true)
                .mustChangePassword(true)
                .merchant(merchant)
                .build();

        MerchantUser saved = merchantUserRepository.save(user);

        return new InviteUserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getRole(),
                tempPassword,
                saved.isMustChangePassword(),
                saved.getCreatedAt()
        );
    }

    @Override
    public DashboardUserInfoResponse updateUserRole(
            UUID userId,
            UserRole newRole
    ) {
        MerchantUser caller = currentJwtUserService.getCurrentUser();
        Merchant merchant = caller.getMerchant();

        MerchantUser target = merchantUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!target.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!target.isEnabled()) {
            throw new ResourceNotFoundException("User not found");
        }

        if (newRole == UserRole.OWNER && caller.getRole() != UserRole.OWNER) {
            throw new ForbiddenException(
                    "Only an OWNER can grant the OWNER role"
            );
        }

        if (target.getRole() == UserRole.OWNER
                && newRole != UserRole.OWNER
                && countOwners(merchant) == 1) {
            throw new BadRequestException(
                    "Cannot change the role of the last remaining OWNER"
            );
        }

        target.setRole(newRole);
        MerchantUser saved = merchantUserRepository.save(target);

        return toUserInfoResponse(saved);
    }

    @Override
    public void deleteUser(UUID userId) {
        MerchantUser caller = currentJwtUserService.getCurrentUser();
        Merchant merchant = caller.getMerchant();

        MerchantUser target = merchantUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!target.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!target.isEnabled()) {
            throw new ResourceNotFoundException("User not found");
        }

        if (target.getId().equals(caller.getId())) {
            throw new BadRequestException(
                    "Cannot remove yourself — ask another owner/admin"
            );
        }

        if (target.getRole() == UserRole.OWNER && countOwners(merchant) == 1) {
            throw new BadRequestException(
                    "Cannot remove the last remaining OWNER"
            );
        }

        target.setEnabled(false);
        merchantUserRepository.save(target);
    }

    private long countOwners(Merchant merchant) {
        return merchantUserRepository
                .findByMerchantOrderByCreatedAtAsc(merchant)
                .stream()
                .filter(MerchantUser::isEnabled)
                .filter(u -> u.getRole() == UserRole.OWNER)
                .count();
    }

    private DashboardUserInfoResponse toUserInfoResponse(MerchantUser user) {
        return new DashboardUserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.isMustChangePassword(),
                user.getCreatedAt()
        );
    }
}
