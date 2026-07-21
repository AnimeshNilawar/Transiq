package com.moddynerd.transiq.dashboard.controller;

import com.moddynerd.transiq.auth.security.AuthenticatedUser;
import com.moddynerd.transiq.merchant.dto.MerchantResponse;
import com.moddynerd.transiq.merchant.enums.MerchantStatus;
import com.moddynerd.transiq.merchant.service.MerchantService;
import com.moddynerd.transiq.shared.exception.UnauthorizedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard/merchants")
@RequiredArgsConstructor
public class DashboardMerchantController {

    private final MerchantService merchantService;

    @PatchMapping("/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MerchantResponse> updateMerchantStatus(
            @RequestBody MerchantStatusRequest request
    ) {
        UUID merchantId = getCurrentMerchantId();
        MerchantResponse response;

        if (request.status() == MerchantStatus.SUSPENDED) {
            response = merchantService.suspendMerchant(merchantId);
        } else if (request.status() == MerchantStatus.ACTIVE) {
            response = merchantService.activateMerchant(merchantId);
        } else {
            throw new IllegalArgumentException("Status must be ACTIVE or SUSPENDED");
        }

        return ResponseEntity.ok(response);
    }

    private UUID getCurrentMerchantId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {
            throw new UnauthorizedException("JWT authentication required");
        }

        return authenticatedUser.getUser().getMerchant().getId();
    }

    public record MerchantStatusRequest(
            @NotNull
            MerchantStatus status
    ) {
    }
}
