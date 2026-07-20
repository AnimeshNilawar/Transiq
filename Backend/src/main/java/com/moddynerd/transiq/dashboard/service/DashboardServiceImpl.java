package com.moddynerd.transiq.dashboard.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.dashboard.dto.DashboardLedgerEntryResponse;
import com.moddynerd.transiq.dashboard.dto.DashboardPageResponse;
import com.moddynerd.transiq.dashboard.dto.DashboardPaymentDetailResponse;
import com.moddynerd.transiq.dashboard.dto.DashboardPaymentSummaryResponse;
import com.moddynerd.transiq.dashboard.specification.*;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.attempt.repository.PaymentAttemptRepository;
import com.moddynerd.transiq.payment.entity.CardPaymentDetails;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.ledger.calculator.BalanceCalculator;
import com.moddynerd.transiq.payment.ledger.dto.MerchantBalanceResponse;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import com.moddynerd.transiq.payment.ledger.repository.LedgerEntryRepository;
import com.moddynerd.transiq.payment.refund.dto.RefundResponse;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import com.moddynerd.transiq.payment.refund.entity.RefundStatus;
import com.moddynerd.transiq.payment.refund.mapper.RefundMapper;
import com.moddynerd.transiq.payment.refund.repository.RefundRepository;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.payment.settlement.dto.SettlementResponse;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;
import com.moddynerd.transiq.payment.settlement.entity.SettlementStatus;
import com.moddynerd.transiq.payment.settlement.mapper.SettlementMapper;
import com.moddynerd.transiq.payment.settlement.repository.SettlementRepository;
import com.moddynerd.transiq.webhook.dto.CreateWebhookRequest;
import com.moddynerd.transiq.webhook.dto.CreateWebhookResponse;
import com.moddynerd.transiq.webhook.dto.WebhookResponse;
import com.moddynerd.transiq.webhook.dto.response.WebhookDeliveryResponse;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.entity.WebhookEventType;
import com.moddynerd.transiq.webhook.mapper.WebhookDeliveryMapper;
import com.moddynerd.transiq.webhook.mapper.WebhookMapper;
import com.moddynerd.transiq.webhook.repository.WebhookDeliveryRepository;
import com.moddynerd.transiq.webhook.repository.WebhookEndpointRepository;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryRetryService;
import com.moddynerd.transiq.webhook.service.WebhookService;
import com.moddynerd.transiq.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final CurrentJwtUserService currentJwtUserService;
    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final RefundRepository refundRepository;
    private final SettlementRepository settlementRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final WebhookEndpointRepository webhookEndpointRepository;
    private final WebhookDeliveryRepository webhookDeliveryRepository;
    private final RefundMapper refundMapper;
    private final SettlementMapper settlementMapper;
    private final WebhookMapper webhookMapper;
    private final WebhookDeliveryMapper webhookDeliveryMapper;
    private final BalanceCalculator balanceCalculator;
    private final PaymentSpecification paymentSpecification;
    private final RefundSpecification refundSpecification;
    private final SettlementSpecification settlementSpecification;
    private final LedgerEntrySpecification ledgerEntrySpecification;
    private final DashboardWebhookDeliverySpecification dashboardWebhookDeliverySpecification;
    private final WebhookDeliveryRetryService webhookDeliveryRetryService;
    private final WebhookService webhookService;
    private final PaymentService paymentService;

    @Override
    public MerchantBalanceResponse getBalance() {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        List<LedgerEntry> entries =
                ledgerEntryRepository.findAllByMerchantOrderByCreatedAtAsc(merchant);

        Long balance = balanceCalculator.calculateMerchantBalance(entries);

        return new MerchantBalanceResponse(balance, "INR");
    }

    @Override
    public DashboardPageResponse<?> getPayments(
            PaymentStatus status,
            Instant from,
            Instant to,
            String orderId,
            Pageable pageable
    ) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        Page<Payment> page = paymentRepository.findAll(
                paymentSpecification.filter(merchant, status, from, to, orderId),
                pageable
        );

        return toPageResponse(page, payment ->
                new DashboardPaymentSummaryResponse(
                        payment.getId(),
                        payment.getPaymentReference(),
                        payment.getAmount(),
                        payment.getCurrency(),
                        payment.getStatus(),
                        payment.getPaymentMethodType(),
                        payment.getCustomerEmail(),
                        payment.getOrderId(),
                        payment.getCreatedAt()
                )
        );
    }

    @Override
    public DashboardPaymentDetailResponse getPaymentDetail(
            String paymentReference
    ) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        Payment payment = paymentRepository
                .findByMerchantAndPaymentReference(merchant, paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        DashboardPaymentDetailResponse.DashboardCardDetailsInfo cardInfo = null;

        if (payment.getCardPaymentDetails() != null) {
            CardPaymentDetails card = payment.getCardPaymentDetails();
            cardInfo = new DashboardPaymentDetailResponse.DashboardCardDetailsInfo(
                    card.getCardNetwork() != null ? card.getCardNetwork().name() : null,
                    card.getIssuerBank() != null ? card.getIssuerBank().name() : null,
                    card.getMaskedCardNumber(),
                    card.getExpiryMonth(),
                    card.getExpiryYear(),
                    card.getAuthorizationCode(),
                    card.getGatewayResponseCode(),
                    card.getGatewayMessage()
            );
        }

        var attempts = paymentAttemptRepository
                .findAllByPaymentOrderByAttemptNumberDesc(payment)
                .stream()
                .map(a -> new DashboardPaymentDetailResponse.DashboardPaymentAttemptInfo(
                        a.getAttemptNumber(),
                        a.getStatus() != null ? a.getStatus().name() : null,
                        a.getFailureCode() != null ? a.getFailureCode().name() : null,
                        a.getFailureMessage(),
                        a.getStartedAt(),
                        a.getCompletedAt(),
                        a.getProcessingTimeMs()
                ))
                .toList();

        return new DashboardPaymentDetailResponse(
                payment.getId(),
                payment.getPaymentReference(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getPaymentMethodType(),
                payment.getCustomerEmail(),
                payment.getCustomerName(),
                payment.getOrderId(),
                payment.getDescription(),
                payment.getCreatedAt(),
                cardInfo,
                attempts
        );
    }

    @Override
    public DashboardPageResponse<?> getRefunds(
            RefundStatus status,
            Instant from,
            Instant to,
            String paymentReference,
            Pageable pageable
    ) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        Page<Refund> page = refundRepository.findAll(
                refundSpecification.filter(merchant, status, from, to, paymentReference),
                pageable
        );

        return toPageResponse(page, refundMapper::toResponse);
    }

    @Override
    public RefundResponse getRefundDetail(String refundReference) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        Refund refund = refundRepository
                .findByRefundReference(refundReference)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));

        if (!refund.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Refund not found");
        }

        return refundMapper.toResponse(refund);
    }

    @Override
    public DashboardPageResponse<?> getSettlements(
            SettlementStatus status,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        Page<Settlement> page = settlementRepository.findAll(
                settlementSpecification.filter(merchant, status, from, to),
                pageable
        );

        return toPageResponse(page, settlementMapper::toResponse);
    }

    @Override
    public SettlementResponse getSettlementDetail(String settlementReference) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        Settlement settlement = settlementRepository
                .findBySettlementReference(settlementReference)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement not found"));

        if (!settlement.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Settlement not found");
        }

        return settlementMapper.toResponse(settlement);
    }

    @Override
    public DashboardPageResponse<?> getLedgerEntries(
            LedgerAccount account,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        Page<LedgerEntry> page = ledgerEntryRepository.findAll(
                ledgerEntrySpecification.filter(merchant, account, from, to),
                pageable
        );

        return toPageResponse(page, entry -> new DashboardLedgerEntryResponse(
                entry.getId(),
                entry.getEntryType(),
                entry.getAccount(),
                entry.getSide(),
                entry.getAmount(),
                entry.getDescription(),
                entry.getFinancialEvent() != null
                        ? entry.getFinancialEvent().getReference()
                        : null,
                entry.getCreatedAt()
        ));
    }

    @Override
    public List<WebhookResponse> getWebhooks() {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        return webhookEndpointRepository
                .findAllByMerchant(merchant)
                .stream()
                .map(webhookMapper::toResponse)
                .toList();
    }

    @Override
    public WebhookResponse getWebhookDetail(UUID webhookId) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        WebhookEndpoint endpoint = webhookEndpointRepository
                .findByIdAndMerchant(webhookId, merchant)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook not found"));

        return webhookMapper.toResponse(endpoint);
    }

    @Override
    public DashboardPageResponse<?> getWebhookDeliveries(
            WebhookDeliveryStatus status,
            WebhookEventType eventType,
            UUID endpointId,
            Instant from,
            Instant to,
            Pageable pageable
    ) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        Page<WebhookDelivery> page = webhookDeliveryRepository.findAll(
                dashboardWebhookDeliverySpecification.filter(
                        merchant, status, eventType, endpointId, from, to
                ),
                pageable
        );

        return toPageResponse(page, webhookDeliveryMapper::toResponse);
    }

    @Override
    public WebhookDeliveryResponse getWebhookDeliveryDetail(UUID deliveryId) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        WebhookDelivery delivery = webhookDeliveryRepository
                .findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook delivery not found"));

        if (!delivery.getEndpoint().getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Webhook delivery not found");
        }

        return webhookDeliveryMapper.toResponse(delivery);
    }

    @Override
    @Transactional
    public void retryWebhookDelivery(UUID deliveryId) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();

        WebhookDelivery delivery = webhookDeliveryRepository
                .findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook delivery not found"));

        if (!delivery.getEndpoint().getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Webhook delivery not found");
        }

        webhookDeliveryRetryService.retry(deliveryId);
    }

    @Override
    @Transactional
    public CreateWebhookResponse createWebhook(CreateWebhookRequest request) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();
        return webhookService.createWebhook(merchant, request);
    }

    @Override
    @Transactional
    public void deleteWebhook(UUID webhookId) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();
        webhookService.disableWebhook(merchant, webhookId);
    }

    @Override
    @Transactional
    public DashboardPaymentDetailResponse retryPayment(String paymentReference) {
        Merchant merchant = currentJwtUserService.getCurrentMerchant();
        paymentService.retryPayment(merchant, paymentReference);
        return getPaymentDetail(paymentReference);
    }

    private <T, R> DashboardPageResponse<R> toPageResponse(
            Page<T> page,
            java.util.function.Function<T, R> mapper
    ) {
        List<R> content = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        return new DashboardPageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
