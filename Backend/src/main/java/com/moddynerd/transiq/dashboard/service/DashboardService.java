package com.moddynerd.transiq.dashboard.service;

import com.moddynerd.transiq.dashboard.dto.DashboardLedgerEntryResponse;
import com.moddynerd.transiq.dashboard.dto.DashboardPageResponse;
import com.moddynerd.transiq.dashboard.dto.DashboardPaymentDetailResponse;
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
import com.moddynerd.transiq.webhook.dto.request.WebhookDeliveryFilter;
import com.moddynerd.transiq.webhook.dto.response.WebhookDeliveryResponse;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEventType;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface DashboardService {

    MerchantBalanceResponse getBalance();

    DashboardPageResponse<?> getPayments(
            PaymentStatus status,
            Instant from,
            Instant to,
            String orderId,
            Pageable pageable
    );

    DashboardPaymentDetailResponse getPaymentDetail(
            String paymentReference
    );

    DashboardPageResponse<?> getRefunds(
            RefundStatus status,
            Instant from,
            Instant to,
            String paymentReference,
            Pageable pageable
    );

    RefundResponse getRefundDetail(String refundReference);

    DashboardPageResponse<?> getSettlements(
            SettlementStatus status,
            Instant from,
            Instant to,
            Pageable pageable
    );

    SettlementResponse getSettlementDetail(String settlementReference);

    DashboardPageResponse<?> getLedgerEntries(
            LedgerAccount account,
            Instant from,
            Instant to,
            Pageable pageable
    );

    java.util.List<WebhookResponse> getWebhooks();

    WebhookResponse getWebhookDetail(UUID webhookId);

    CreateWebhookResponse createWebhook(CreateWebhookRequest request);

    void deleteWebhook(UUID webhookId);

    DashboardPageResponse<?> getWebhookDeliveries(
            WebhookDeliveryStatus status,
            WebhookEventType eventType,
            UUID endpointId,
            Instant from,
            Instant to,
            Pageable pageable
    );

    WebhookDeliveryResponse getWebhookDeliveryDetail(UUID deliveryId);

    void retryWebhookDelivery(UUID deliveryId);

    DashboardPaymentDetailResponse retryPayment(String paymentReference);
}
