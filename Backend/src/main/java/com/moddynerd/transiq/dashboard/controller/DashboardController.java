package com.moddynerd.transiq.dashboard.controller;

import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.dashboard.dto.DashboardMeResponse;
import com.moddynerd.transiq.dashboard.dto.DashboardPageResponse;
import com.moddynerd.transiq.dashboard.dto.DashboardPaymentDetailResponse;
import com.moddynerd.transiq.dashboard.service.CurrentJwtUserService;
import com.moddynerd.transiq.dashboard.service.DashboardService;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.ledger.dto.MerchantBalanceResponse;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.refund.dto.RefundResponse;
import com.moddynerd.transiq.payment.refund.entity.RefundStatus;
import com.moddynerd.transiq.payment.settlement.dto.SettlementResponse;
import com.moddynerd.transiq.payment.settlement.entity.SettlementStatus;
import com.moddynerd.transiq.webhook.dto.CreateWebhookRequest;
import com.moddynerd.transiq.webhook.dto.CreateWebhookResponse;
import com.moddynerd.transiq.webhook.dto.WebhookResponse;
import com.moddynerd.transiq.webhook.dto.response.WebhookDeliveryResponse;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEventType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CurrentJwtUserService currentJwtUserService;
    private final DashboardService dashboardService;

    @GetMapping("/me")
    public ResponseEntity<DashboardMeResponse> getMe() {
        MerchantUser user = currentJwtUserService.getCurrentUser();

        DashboardMeResponse response = new DashboardMeResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                new DashboardMeResponse.DashboardMerchantInfo(
                        user.getMerchant().getId(),
                        user.getMerchant().getBusinessName(),
                        user.getMerchant().getBusinessEmail(),
                        user.getMerchant().getStatus()
                )
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments")
    public ResponseEntity<DashboardPageResponse<?>> getPayments(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Pageable pageable = parsePageable(page, size, sort);

        return ResponseEntity.ok(
                dashboardService.getPayments(status, from, to, orderId, pageable)
        );
    }

    @GetMapping("/payments/{paymentReference}")
    public ResponseEntity<DashboardPaymentDetailResponse> getPaymentDetail(
            @PathVariable String paymentReference
    ) {
        return ResponseEntity.ok(
                dashboardService.getPaymentDetail(paymentReference)
        );
    }

    @GetMapping("/refunds")
    public ResponseEntity<DashboardPageResponse<?>> getRefunds(
            @RequestParam(required = false) RefundStatus status,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String paymentReference,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Pageable pageable = parsePageable(page, size, sort);

        return ResponseEntity.ok(
                dashboardService.getRefunds(status, from, to, paymentReference, pageable)
        );
    }

    @GetMapping("/refunds/{refundReference}")
    public ResponseEntity<RefundResponse> getRefundDetail(
            @PathVariable String refundReference
    ) {
        return ResponseEntity.ok(
                dashboardService.getRefundDetail(refundReference)
        );
    }

    @GetMapping("/settlements")
    public ResponseEntity<DashboardPageResponse<?>> getSettlements(
            @RequestParam(required = false) SettlementStatus status,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Pageable pageable = parsePageable(page, size, sort);

        return ResponseEntity.ok(
                dashboardService.getSettlements(status, from, to, pageable)
        );
    }

    @GetMapping("/settlements/{settlementReference}")
    public ResponseEntity<SettlementResponse> getSettlementDetail(
            @PathVariable String settlementReference
    ) {
        return ResponseEntity.ok(
                dashboardService.getSettlementDetail(settlementReference)
        );
    }

    @GetMapping("/ledger/balance")
    public ResponseEntity<MerchantBalanceResponse> getLedgerBalance() {
        return ResponseEntity.ok(
                dashboardService.getBalance()
        );
    }

    @GetMapping("/ledger/entries")
    public ResponseEntity<DashboardPageResponse<?>> getLedgerEntries(
            @RequestParam(required = false) LedgerAccount account,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Pageable pageable = parsePageable(page, size, sort);

        return ResponseEntity.ok(
                dashboardService.getLedgerEntries(account, from, to, pageable)
        );
    }

    @GetMapping("/webhooks")
    public ResponseEntity<List<WebhookResponse>> getWebhooks() {
        return ResponseEntity.ok(
                dashboardService.getWebhooks()
        );
    }

    @GetMapping("/webhooks/{id}")
    public ResponseEntity<WebhookResponse> getWebhookDetail(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                dashboardService.getWebhookDetail(id)
        );
    }

    @GetMapping("/webhooks/deliveries")
    public ResponseEntity<DashboardPageResponse<?>> getWebhookDeliveries(
            @RequestParam(required = false) WebhookDeliveryStatus status,
            @RequestParam(required = false) WebhookEventType eventType,
            @RequestParam(required = false) UUID endpointId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Pageable pageable = parsePageable(page, size, sort);

        return ResponseEntity.ok(
                dashboardService.getWebhookDeliveries(
                        status, eventType, endpointId, from, to, pageable
                )
        );
    }

    @GetMapping("/webhooks/deliveries/{id}")
    public ResponseEntity<WebhookDeliveryResponse> getWebhookDeliveryDetail(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                dashboardService.getWebhookDeliveryDetail(id)
        );
    }

    @PostMapping("/webhooks/deliveries/{id}/retry")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void retryWebhookDelivery(@PathVariable UUID id) {
        dashboardService.retryWebhookDelivery(id);
    }

    @PostMapping("/webhooks")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateWebhookResponse createWebhook(
            @Valid
            @RequestBody
            CreateWebhookRequest request
    ) {
        return dashboardService.createWebhook(request);
    }

    @DeleteMapping("/webhooks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWebhook(@PathVariable UUID id) {
        dashboardService.deleteWebhook(id);
    }

    @PostMapping("/payments/{paymentReference}/retry")
    public ResponseEntity<DashboardPaymentDetailResponse> retryPayment(
            @PathVariable String paymentReference
    ) {
        return ResponseEntity.ok(
                dashboardService.retryPayment(paymentReference)
        );
    }

    private Pageable parsePageable(int page, int size, String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = parts.length > 1
                && parts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
