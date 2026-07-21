package com.moddynerd.transiq.admin.service;

import com.moddynerd.transiq.admin.dto.*;
import com.moddynerd.transiq.apikey.entity.ApiKey;
import com.moddynerd.transiq.apikey.entity.ApiKeyStatus;
import com.moddynerd.transiq.apikey.repository.ApiKeyRepository;
import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.auth.repository.MerchantUserRepository;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.merchant.repository.MerchantRepository;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import com.moddynerd.transiq.payment.refund.repository.RefundRepository;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;
import com.moddynerd.transiq.payment.settlement.repository.SettlementRepository;
import com.moddynerd.transiq.payment.settlement.service.SettlementService;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.repository.WebhookDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final MerchantRepository merchantRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementService settlementService;
    private final MerchantUserRepository merchantUserRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final WebhookDeliveryRepository webhookDeliveryRepository;

    public AdminDashboardResponse getDashboard() {
        long totalMerchants = merchantRepository.count();
        long totalPayments = paymentRepository.count();
        long totalVolume = paymentRepository.sumAmountByStatus(PaymentStatus.SUCCEEDED) != null
                ? paymentRepository.sumAmountByStatus(PaymentStatus.SUCCEEDED)
                : 0L;
        long totalRefunds = 0L;

        var recent = paymentRepository
                .findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(p -> new AdminDashboardResponse.RecentPayment(
                        p.getPaymentReference(),
                        p.getAmount(),
                        p.getCurrency().name(),
                        p.getStatus().name(),
                        p.getMerchant().getBusinessName(),
                        p.getCreatedAt().toString()
                ))
                .toList();

        return new AdminDashboardResponse(
                totalMerchants,
                totalPayments,
                totalVolume,
                totalRefunds,
                0L,
                recent
        );
    }

    public Page<AdminMerchantResponse> getMerchants(Pageable pageable) {
        return merchantRepository.findAll(pageable)
                .map(m -> new AdminMerchantResponse(
                        m.getId(),
                        m.getBusinessName(),
                        m.getBusinessEmail(),
                        m.getStatus() != null ? m.getStatus().name() : null,
                        m.getCreatedAt()
                ));
    }

    public AdminMerchantDetailResponse getMerchantDetail(UUID id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
        int userCount = merchantUserRepository.findByMerchantOrderByCreatedAtAsc(merchant).size();
        int apiKeyCount = apiKeyRepository.findByMerchant(merchant).size();
        long totalVolume = paymentRepository.sumAmountByMerchantAndStatus(merchant, PaymentStatus.SUCCEEDED);
        long totalCount = paymentRepository.countByMerchant(merchant);
        return new AdminMerchantDetailResponse(
                merchant.getId(),
                merchant.getBusinessName(),
                merchant.getBusinessEmail(),
                merchant.getStatus().name(),
                userCount,
                apiKeyCount,
                totalVolume,
                totalCount,
                merchant.getCreatedAt()
        );
    }

    public Page<AdminPaymentResponse> getPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(p -> new AdminPaymentResponse(
                        p.getId(),
                        p.getPaymentReference(),
                        p.getAmount(),
                        p.getCurrency().name(),
                        p.getStatus().name(),
                        p.getMerchant().getId(),
                        p.getMerchant().getBusinessName(),
                        p.getCustomerEmail(),
                        p.getCustomerName(),
                        p.getCreatedAt()
                ));
    }

    public AdminPaymentDetailResponse getPaymentDetail(String reference) {
        Payment p = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return new AdminPaymentDetailResponse(
                p.getId(),
                p.getPaymentReference(),
                p.getAmount(),
                p.getRefundedAmount(),
                p.getCurrency().name(),
                p.getStatus().name(),
                p.getPaymentMethodType() != null ? p.getPaymentMethodType().name() : null,
                p.getMerchant().getId(),
                p.getMerchant().getBusinessName(),
                p.getMerchant().getBusinessEmail(),
                p.getCustomerEmail(),
                p.getCustomerName(),
                p.getOrderId(),
                p.getDescription(),
                p.getIdempotencyKey(),
                p.getCreatedAt(),
                p.getExpiresAt()
        );
    }

    @Transactional
    public void updatePaymentStatus(String reference, UpdatePaymentStatusRequest request) {
        Payment p = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        p.setStatus(PaymentStatus.valueOf(request.status()));
        paymentRepository.save(p);
    }

    public Page<AdminRefundResponse> getRefunds(Pageable pageable) {
        return refundRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(r -> new AdminRefundResponse(
                        r.getId(),
                        r.getRefundReference(),
                        r.getAmount(),
                        r.getStatus().name(),
                        r.getReason().name(),
                        r.getPayment().getPaymentReference(),
                        r.getMerchant().getId(),
                        r.getMerchant().getBusinessName(),
                        r.getCreatedAt()
                ));
    }

    public Page<AdminSettlementResponse> getSettlements(Pageable pageable) {
        return settlementRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(s -> new AdminSettlementResponse(
                        s.getId(),
                        s.getSettlementReference(),
                        s.getAmount(),
                        s.getCurrency(),
                        s.getStatus().name(),
                        s.getMerchant().getId(),
                        s.getMerchant().getBusinessName(),
                        s.getProcessedAt(),
                        s.getBankReference(),
                        s.getCreatedAt()
                ));
    }

    @Transactional
    public void createSettlementForMerchant(UUID merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
        settlementService.createSettlementForMerchant(merchant);
    }

    public Page<AdminUserResponse> getUsers(Pageable pageable) {
        return merchantUserRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(u -> new AdminUserResponse(
                        u.getId(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getEmail(),
                        u.getRole().name(),
                        u.isEnabled(),
                        u.getMerchant().getId(),
                        u.getMerchant().getBusinessName(),
                        u.getCreatedAt()
                ));
    }

    @Transactional
    public void updateUserStatus(UUID userId, boolean enabled) {
        MerchantUser user = merchantUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(enabled);
        merchantUserRepository.save(user);
    }

    public Page<AdminApiKeyResponse> getApiKeys(Pageable pageable) {
        return apiKeyRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(k -> new AdminApiKeyResponse(
                        k.getId(),
                        k.getName(),
                        k.getKeyPrefix(),
                        k.getType().name(),
                        k.getEnvironment().name(),
                        k.getStatus().name(),
                        k.getMerchant().getId(),
                        k.getMerchant().getBusinessName(),
                        k.getLastUsedAt(),
                        k.getCreatedAt()
                ));
    }

    @Transactional
    public void revokeApiKey(UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("API key not found"));
        key.setStatus(ApiKeyStatus.REVOKED);
        apiKeyRepository.save(key);
    }

    public Page<AdminWebhookDeliveryResponse> getWebhookDeliveries(Pageable pageable) {
        return webhookDeliveryRepository.findAllWithEndpointAndMerchant(pageable)
                .map(d -> new AdminWebhookDeliveryResponse(
                        d.getId(),
                        d.getStatus().name(),
                        d.getHttpStatus(),
                        d.getEventType(),
                        d.getEventReference(),
                        d.getEndpoint().getUrl(),
                        d.getEndpoint().getMerchant().getId(),
                        d.getEndpoint().getMerchant().getBusinessName(),
                        d.getAttemptCount(),
                        d.getFailureReason(),
                        d.getCreatedAt(),
                        d.getLastAttemptAt(),
                        d.getNextRetryAt()
                ));
    }

    @Transactional
    public void retryWebhookDelivery(UUID deliveryId) {
        WebhookDelivery delivery = webhookDeliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook delivery not found"));
        delivery.setStatus(com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus.PENDING);
        delivery.setNextRetryAt(Instant.now());
        webhookDeliveryRepository.save(delivery);
    }

    public List<RevenueDataPoint> getRevenueTimeSeries() {
        Instant since = Instant.now().minus(java.time.Duration.ofDays(30));
        return paymentRepository.revenueTimeSeries(since).stream()
                .map(row -> new RevenueDataPoint(
                        row[0].toString(),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }

    public List<FailureTrendDataPoint> getFailureTrend() {
        Instant since = Instant.now().minus(java.time.Duration.ofDays(30));
        return paymentRepository.failureTrend(since).stream()
                .map(row -> new FailureTrendDataPoint(
                        row[0].toString(),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }

    public List<AdminAlertResponse> getAlerts() {
        List<AdminAlertResponse> alerts = new ArrayList<>();

        Instant oneDayAgo = Instant.now().minus(java.time.Duration.ofHours(24));
        List<Object[]> suspicious = paymentRepository.suspiciousActivity(oneDayAgo);
        for (Object[] row : suspicious) {
            UUID merchantId = (UUID) row[0];
            String name = (String) row[1];
            long total = ((Number) row[2]).longValue();
            long failed = ((Number) row[3]).longValue();
            alerts.add(new AdminAlertResponse(
                    "HIGH_FAILURE_RATE",
                    "warning",
                    String.format("%d/%d payments failed (%.0f%%) in last 24h",
                            failed, total, (failed * 100.0 / total)),
                    merchantId, name, Instant.now()
            ));
        }

        List<Object[]> failedWebhooks = webhookDeliveryRepository.countFailedByMerchant();
        for (Object[] row : failedWebhooks) {
            UUID merchantId = (UUID) row[0];
            String name = (String) row[1];
            long count = ((Number) row[2]).longValue();
            alerts.add(new AdminAlertResponse(
                    "STALLED_WEBHOOKS",
                    "error",
                    String.format("%d webhook deliveries stuck in FAILED", count),
                    merchantId, name, Instant.now()
            ));
        }

        return alerts;
    }
}
