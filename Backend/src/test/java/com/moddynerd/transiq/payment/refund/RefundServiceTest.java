package com.moddynerd.transiq.payment.refund;

import com.moddynerd.transiq.apikey.entity.ApiKeyEnvironment;
import com.moddynerd.transiq.apikey.entity.ApiKeyType;
import com.moddynerd.transiq.apikey.security.ApiKeyPrincipal;
import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.event.publisher.DomainEventPublisher;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.merchant.enums.MerchantStatus;
import com.moddynerd.transiq.payment.entity.Currency;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.refund.dto.CreateRefundRequest;
import com.moddynerd.transiq.payment.refund.dto.CreateRefundResponse;
import com.moddynerd.transiq.payment.refund.dto.RefundResponse;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import com.moddynerd.transiq.payment.refund.entity.RefundReason;
import com.moddynerd.transiq.payment.refund.entity.RefundStatus;
import com.moddynerd.transiq.payment.refund.mapper.RefundMapper;
import com.moddynerd.transiq.payment.refund.repository.RefundRepository;
import com.moddynerd.transiq.payment.refund.service.RefundServiceImpl;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.shared.exception.ConflictException;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RefundMapper refundMapper;
    @Mock
    private CurrentApiKeyService currentApiKeyService;
    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private RefundServiceImpl refundService;

    private Merchant merchant;
    private ApiKeyPrincipal apiKeyPrincipal;
    private Payment succeededPayment;

    @BeforeEach
    void setUp() {
        merchant = Merchant.builder()
                .id(UUID.randomUUID())
                .businessName("Acme Corp")
                .businessEmail("billing@acme.com")
                .status(MerchantStatus.ACTIVE)
                .build();

        apiKeyPrincipal = new ApiKeyPrincipal(
                UUID.randomUUID(),
                merchant,
                ApiKeyType.SECRET,
                ApiKeyEnvironment.LIVE
        );

        succeededPayment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_123")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.SUCCEEDED)
                .refundedAmount(0L)
                .clientSecretHash("hashed")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();
    }

    @Test
    void createRefund_success() {
        CreateRefundRequest request = new CreateRefundRequest(500L, RefundReason.REQUESTED_BY_CUSTOMER);

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(succeededPayment));
        when(refundRepository.findByPaymentAndIdempotencyKey(succeededPayment, "idem-refund-1"))
                .thenReturn(Optional.empty());
        when(refundRepository.findByRefundReference(anyString())).thenReturn(Optional.empty());

        Refund savedRefund = Refund.builder()
                .id(UUID.randomUUID())
                .payment(succeededPayment)
                .merchant(merchant)
                .refundReference("ref_123")
                .amount(500L)
                .reason(RefundReason.REQUESTED_BY_CUSTOMER)
                .status(RefundStatus.SUCCEEDED)
                .idempotencyKey("idem-refund-1")
                .build();

        when(refundRepository.save(any(Refund.class)))
                .thenReturn(savedRefund);

        CreateRefundResponse expectedResponse = new CreateRefundResponse("ref_123", 500L, "SUCCEEDED");
        when(refundMapper.toCreateResponse(savedRefund)).thenReturn(expectedResponse);

        CreateRefundResponse result = refundService.createRefund("pay_ref_123", "idem-refund-1", request);

        assertThat(result).isNotNull();
        assertThat(result.refundReference()).isEqualTo("ref_123");
        assertThat(result.amount()).isEqualTo(500L);
        verify(domainEventPublisher).publish(any());
        verify(paymentRepository).save(argThat(payment ->
                payment.getRefundedAmount().equals(500L)
                        && payment.getStatus() == PaymentStatus.SUCCEEDED
        ));
    }

    @Test
    void createRefund_fullRefund_setsRefundedStatus() {
        CreateRefundRequest request = new CreateRefundRequest(1000L, RefundReason.FRAUDULENT);

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(succeededPayment));
        when(refundRepository.findByPaymentAndIdempotencyKey(succeededPayment, "idem-refund-full"))
                .thenReturn(Optional.empty());
        when(refundRepository.findByRefundReference(anyString())).thenReturn(Optional.empty());

        Refund savedRefund = Refund.builder()
                .id(UUID.randomUUID())
                .payment(succeededPayment)
                .merchant(merchant)
                .refundReference("ref_full")
                .amount(1000L)
                .reason(RefundReason.FRAUDULENT)
                .status(RefundStatus.SUCCEEDED)
                .idempotencyKey("idem-refund-full")
                .build();

        when(refundRepository.save(any(Refund.class))).thenReturn(savedRefund);

        CreateRefundResponse expectedResponse = new CreateRefundResponse("ref_full", 1000L, "SUCCEEDED");
        when(refundMapper.toCreateResponse(savedRefund)).thenReturn(expectedResponse);

        CreateRefundResponse result = refundService.createRefund("pay_ref_123", "idem-refund-full", request);

        assertThat(result).isNotNull();
        verify(paymentRepository).save(argThat(payment ->
                payment.getStatus() == PaymentStatus.REFUNDED
        ));
    }

    @Test
    void createRefund_onNonSucceededPayment_shouldFail() {
        Payment processingPayment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_456")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.PROCESSING)
                .refundedAmount(0L)
                .clientSecretHash("hashed")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        CreateRefundRequest request = new CreateRefundRequest(500L, RefundReason.REQUESTED_BY_CUSTOMER);

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_456"))
                .thenReturn(Optional.of(processingPayment));

        assertThatThrownBy(() -> refundService.createRefund("pay_ref_456", "idem-key", request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Only successful payments can be refunded");
    }

    @Test
    void createRefund_paymentNotFound_shouldFail() {
        CreateRefundRequest request = new CreateRefundRequest(500L, RefundReason.REQUESTED_BY_CUSTOMER);

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "nonexistent"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> refundService.createRefund("nonexistent", "idem-key", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    void createRefund_withIdempotencyKey_returnsExistingRefund() {
        CreateRefundRequest request = new CreateRefundRequest(500L, RefundReason.REQUESTED_BY_CUSTOMER);

        Refund existingRefund = Refund.builder()
                .id(UUID.randomUUID())
                .payment(succeededPayment)
                .merchant(merchant)
                .refundReference("ref_existing")
                .amount(500L)
                .reason(RefundReason.REQUESTED_BY_CUSTOMER)
                .status(RefundStatus.SUCCEEDED)
                .idempotencyKey("idem-key-dup")
                .build();

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(succeededPayment));
        when(refundRepository.findByPaymentAndIdempotencyKey(succeededPayment, "idem-key-dup"))
                .thenReturn(Optional.of(existingRefund));

        CreateRefundResponse expectedResponse = new CreateRefundResponse("ref_existing", 500L, "SUCCEEDED");
        when(refundMapper.toCreateResponse(existingRefund)).thenReturn(expectedResponse);

        CreateRefundResponse result = refundService.createRefund("pay_ref_123", "idem-key-dup", request);

        assertThat(result).isNotNull();
        assertThat(result.refundReference()).isEqualTo("ref_existing");
        verify(refundRepository, never()).save(any(Refund.class));
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    void createRefund_amountExceedsRemainingBalance_shouldFail() {
        Payment partiallyRefundedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_789")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.SUCCEEDED)
                .refundedAmount(800L)
                .clientSecretHash("hashed")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        CreateRefundRequest request = new CreateRefundRequest(500L, RefundReason.DUPLICATE_PAYMENT);

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_789"))
                .thenReturn(Optional.of(partiallyRefundedPayment));
        when(refundRepository.findByPaymentAndIdempotencyKey(partiallyRefundedPayment, "idem-key-over"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> refundService.createRefund("pay_ref_789", "idem-key-over", request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Refund amount exceeds remaining refundable balance");
    }

    @Test
    void getRefund_success() {
        Refund refund = Refund.builder()
                .id(UUID.randomUUID())
                .payment(succeededPayment)
                .merchant(merchant)
                .refundReference("ref_123")
                .amount(500L)
                .reason(RefundReason.REQUESTED_BY_CUSTOMER)
                .status(RefundStatus.SUCCEEDED)
                .idempotencyKey("idem-key")
                .build();

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(refundRepository.findByRefundReference("ref_123"))
                .thenReturn(Optional.of(refund));

        RefundResponse expectedResponse = new RefundResponse(
                "ref_123", "pay_ref_123", 500L, RefundStatus.SUCCEEDED, RefundReason.REQUESTED_BY_CUSTOMER, Instant.now()
        );
        when(refundMapper.toResponse(refund)).thenReturn(expectedResponse);

        RefundResponse result = refundService.getRefund("ref_123");

        assertThat(result).isNotNull();
        assertThat(result.refundReference()).isEqualTo("ref_123");
        assertThat(result.amount()).isEqualTo(500L);
    }

    @Test
    void getRefund_nonExistentRefund_shouldFail() {
        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(refundRepository.findByRefundReference("nonexistent"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> refundService.getRefund("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Refund not found");
    }

    @Test
    void getRefund_crossMerchant_shouldFail() {
        Merchant otherMerchant = Merchant.builder()
                .id(UUID.randomUUID())
                .businessName("Other Corp")
                .businessEmail("other@corp.com")
                .status(MerchantStatus.ACTIVE)
                .build();

        Refund refund = Refund.builder()
                .id(UUID.randomUUID())
                .payment(succeededPayment)
                .merchant(otherMerchant)
                .refundReference("ref_cross")
                .amount(500L)
                .reason(RefundReason.REQUESTED_BY_CUSTOMER)
                .status(RefundStatus.SUCCEEDED)
                .idempotencyKey("idem-key")
                .build();

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(refundRepository.findByRefundReference("ref_cross"))
                .thenReturn(Optional.of(refund));

        assertThatThrownBy(() -> refundService.getRefund("ref_cross"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Refund not found");
    }
}
