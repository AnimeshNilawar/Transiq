package com.moddynerd.transiq.admin.controller;

import com.moddynerd.transiq.admin.dto.*;
import com.moddynerd.transiq.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboard();
    }

    @GetMapping("/merchants")
    public Page<AdminMerchantResponse> getMerchants(Pageable pageable) {
        return adminService.getMerchants(pageable);
    }

    @GetMapping("/merchants/{id}")
    public AdminMerchantDetailResponse getMerchantDetail(@PathVariable UUID id) {
        return adminService.getMerchantDetail(id);
    }

    @GetMapping("/payments")
    public Page<AdminPaymentResponse> getPayments(Pageable pageable) {
        return adminService.getPayments(pageable);
    }

    @GetMapping("/payments/{reference}")
    public AdminPaymentDetailResponse getPaymentDetail(@PathVariable String reference) {
        return adminService.getPaymentDetail(reference);
    }

    @PatchMapping("/payments/{reference}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePaymentStatus(
            @PathVariable String reference,
            @Valid @RequestBody UpdatePaymentStatusRequest request
    ) {
        adminService.updatePaymentStatus(reference, request);
    }

    @GetMapping("/refunds")
    public Page<AdminRefundResponse> getRefunds(Pageable pageable) {
        return adminService.getRefunds(pageable);
    }

    @GetMapping("/settlements")
    public Page<AdminSettlementResponse> getSettlements(Pageable pageable) {
        return adminService.getSettlements(pageable);
    }

    @PostMapping("/settlements")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSettlement(@RequestParam UUID merchantId) {
        adminService.createSettlementForMerchant(merchantId);
    }

    @GetMapping("/users")
    public Page<AdminUserResponse> getUsers(Pageable pageable) {
        return adminService.getUsers(pageable);
    }

    @PatchMapping("/users/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserStatus(
            @PathVariable UUID id,
            @RequestParam boolean enabled
    ) {
        adminService.updateUserStatus(id, enabled);
    }

    @GetMapping("/api-keys")
    public Page<AdminApiKeyResponse> getApiKeys(Pageable pageable) {
        return adminService.getApiKeys(pageable);
    }

    @DeleteMapping("/api-keys/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeApiKey(@PathVariable UUID id) {
        adminService.revokeApiKey(id);
    }

    @GetMapping("/webhook-deliveries")
    public Page<AdminWebhookDeliveryResponse> getWebhookDeliveries(Pageable pageable) {
        return adminService.getWebhookDeliveries(pageable);
    }

    @PostMapping("/webhook-deliveries/{id}/retry")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retryWebhookDelivery(@PathVariable UUID id) {
        adminService.retryWebhookDelivery(id);
    }

    @GetMapping("/analytics/revenue")
    public List<RevenueDataPoint> getRevenueTimeSeries() {
        return adminService.getRevenueTimeSeries();
    }

    @GetMapping("/analytics/failure-trends")
    public List<FailureTrendDataPoint> getFailureTrend() {
        return adminService.getFailureTrend();
    }

    @GetMapping("/alerts")
    public List<AdminAlertResponse> getAlerts() {
        return adminService.getAlerts();
    }
}
