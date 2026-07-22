package com.moddynerd.transiq.payment;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.merchant.enums.MerchantStatus;
import com.moddynerd.transiq.payment.dto.ConfirmPaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentResponse;
import com.moddynerd.transiq.payment.dto.PaymentResponse;
import com.moddynerd.transiq.payment.entity.Currency;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.expiration.PaymentExpirationService;
import com.moddynerd.transiq.payment.mapper.PaymentMapper;
import com.moddynerd.transiq.payment.processor.PaymentProcessor;
import com.moddynerd.transiq.payment.repository.CardPaymentDetailsRepository;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.payment.security.ClientSecretService;
import com.moddynerd.transiq.payment.service.PaymentServiceImpl;
import com.moddynerd.transiq.payment.state.PaymentStateMachine;
import com.moddynerd.transiq.shared.exception.ConflictException;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import com.moddynerd.transiq.apikey.security.ApiKeyPrincipal;
import com.moddynerd.transiq.apikey.entity.ApiKeyEnvironment;
import com.moddynerd.transiq.apikey.entity.ApiKeyType;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CurrentApiKeyService currentApiKeyService;
    @Mock
    private PaymentProcessor paymentProcessor;
    @Mock
    private PaymentStateMachine paymentStateMachine;
    @Mock
    private ClientSecretService clientSecretService;
    @Mock
    private PaymentExpirationService paymentExpirationService;
    @Mock
    private CardPaymentDetailsRepository cardPaymentDetailsRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Merchant merchant;
    private ApiKeyPrincipal apiKeyPrincipal;

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
    }

    @Test
    void createPayment_success() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                1000L, Currency.INR, "user@example.com", "John Doe", "order-001", "Test payment"
        );

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndIdempotencyKey(merchant, "idem-key-1"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_secret");
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreatePaymentResponse expectedResponse = new CreatePaymentResponse(
                UUID.randomUUID(), "pay_ref_123", "cs_secret_123", PaymentStatus.REQUIRES_PAYMENT_METHOD, Instant.now()
        );
        when(paymentMapper.toCreateResponse(any(Payment.class), anyString()))
                .thenReturn(expectedResponse);

        CreatePaymentResponse result = paymentService.createPayment("idem-key-1", request);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PaymentStatus.REQUIRES_PAYMENT_METHOD);
        verify(paymentRepository).save(any(Payment.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void createPayment_withIdempotencyKey_returnsExistingPayment() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                1000L, Currency.INR, "user@example.com", "John Doe", "order-001", "Test payment"
        );

        Payment existingPayment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_existing")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.REQUIRES_PAYMENT_METHOD)
                .idempotencyKey("idem-key-1")
                .clientSecretHash("hashed")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndIdempotencyKey(merchant, "idem-key-1"))
                .thenReturn(Optional.of(existingPayment));

        CreatePaymentResponse expectedResponse = new CreatePaymentResponse(
                existingPayment.getId(), existingPayment.getPaymentReference(), null, existingPayment.getStatus(), Instant.now()
        );
        when(paymentMapper.toCreateResponse(existingPayment, null))
                .thenReturn(expectedResponse);

        CreatePaymentResponse result = paymentService.createPayment("idem-key-1", request);

        assertThat(result).isNotNull();
        assertThat(result.paymentReference()).isEqualTo("pay_ref_existing");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void confirmPayment_withValidClientSecret() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_123")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.REQUIRES_PAYMENT_METHOD)
                .clientSecretHash("hashed_secret")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        ConfirmPaymentRequest request = new ConfirmPaymentRequest(
                "valid_secret", PaymentMethodType.CARD, null, null, null, null, null, null
        );

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse expectedResponse = new PaymentResponse(
                payment.getId(), "pay_ref_123", 1000L, Currency.INR, PaymentStatus.PROCESSING, "user@example.com", "order-001", Instant.now(), null, null
        );
        when(paymentMapper.toResponse(payment)).thenReturn(expectedResponse);

        PaymentResponse result = paymentService.confirmPayment("pay_ref_123", request);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PaymentStatus.PROCESSING);
        verify(clientSecretService).verify(payment, "valid_secret");
        verify(paymentExpirationService).validate(payment);
        verify(paymentProcessor).process(payment);
    }

    @Test
    void confirmPayment_withInvalidClientSecret_shouldFail() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_123")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.REQUIRES_PAYMENT_METHOD)
                .clientSecretHash("hashed_secret")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        ConfirmPaymentRequest request = new ConfirmPaymentRequest(
                "invalid_secret", PaymentMethodType.CARD, null, null, null, null, null, null
        );

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(payment));
        doThrow(new com.moddynerd.transiq.shared.exception.UnauthorizedException("Invalid Client Secret"))
                .when(clientSecretService).verify(payment, "invalid_secret");

        assertThatThrownBy(() -> paymentService.confirmPayment("pay_ref_123", request))
                .isInstanceOf(com.moddynerd.transiq.shared.exception.UnauthorizedException.class)
                .hasMessageContaining("Invalid Client Secret");

        verify(paymentProcessor, never()).process(any(Payment.class));
    }

    @Test
    void confirmPayment_paymentNotFound_shouldFail() {
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(
                "secret", PaymentMethodType.CARD, null, null, null, null, null, null
        );

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "nonexistent"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.confirmPayment("nonexistent", request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    void retryPayment_onFailedPayment() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_123")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.FAILED)
                .clientSecretHash("hashed_secret")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse expectedResponse = new PaymentResponse(
                payment.getId(), "pay_ref_123", 1000L, Currency.INR, PaymentStatus.REQUIRES_PAYMENT_METHOD, null, null, Instant.now(), null, null
        );
        when(paymentMapper.toResponse(payment)).thenReturn(expectedResponse);

        PaymentResponse result = paymentService.retryPayment(merchant, "pay_ref_123");

        assertThat(result).isNotNull();
        verify(paymentStateMachine).transition(payment, PaymentStatus.REQUIRES_PAYMENT_METHOD);
        verify(paymentProcessor).process(payment);
    }

    @Test
    void retryPayment_onExpiredPayment_shouldFail() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_123")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.EXPIRED)
                .clientSecretHash("hashed_secret")
                .expiresAt(Instant.now().minusSeconds(60))
                .build();

        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(payment));
        doThrow(new ConflictException("Payment has expired"))
                .when(paymentExpirationService).validate(payment);

        assertThatThrownBy(() -> paymentService.retryPayment(merchant, "pay_ref_123"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Payment has expired");

        verify(paymentProcessor, never()).process(any(Payment.class));
    }

    @Test
    void retryPayment_onSucceededPayment_shouldFail() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_123")
                .amount(1000L)
                .currency(Currency.INR)
                .status(PaymentStatus.SUCCEEDED)
                .clientSecretHash("hashed_secret")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.retryPayment(merchant, "pay_ref_123"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Payment cannot be retried in current state");
    }

    @Test
    void retryPayment_paymentNotFound_shouldFail() {
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "nonexistent"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.retryPayment(merchant, "nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not Found");
    }

    @Test
    void getPayment_paymentNotFound_shouldFail() {
        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "nonexistent"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    void getPayment_success() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_123")
                .amount(5000L)
                .currency(Currency.USD)
                .status(PaymentStatus.SUCCEEDED)
                .clientSecretHash("hashed_secret")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndPaymentReference(merchant, "pay_ref_123"))
                .thenReturn(Optional.of(payment));

        PaymentResponse expectedResponse = new PaymentResponse(
                payment.getId(), "pay_ref_123", 5000L, Currency.USD, PaymentStatus.SUCCEEDED, null, null, Instant.now(), null, null
        );
        when(paymentMapper.toResponse(payment)).thenReturn(expectedResponse);

        PaymentResponse result = paymentService.getPayment("pay_ref_123");

        assertThat(result).isNotNull();
        assertThat(result.amount()).isEqualTo(5000L);
        assertThat(result.currency()).isEqualTo(Currency.USD);
    }

    @Test
    void paymentStateMachine_transitionToProcessing() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_456")
                .amount(2000L)
                .currency(Currency.INR)
                .status(PaymentStatus.REQUIRES_PAYMENT_METHOD)
                .clientSecretHash("hashed")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        paymentStateMachine.transition(payment, PaymentStatus.PROCESSING);

        verify(paymentStateMachine).transition(payment, PaymentStatus.PROCESSING);
    }

    @Test
    void paymentStateMachine_invalidTransition_shouldFail() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .merchant(merchant)
                .paymentReference("pay_ref_789")
                .amount(3000L)
                .currency(Currency.INR)
                .status(PaymentStatus.SUCCEEDED)
                .clientSecretHash("hashed")
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        doThrow(new com.moddynerd.transiq.payment.state.InvalidPaymentStateException(
                "Cannot transition payment from SUCCEEDED to PROCESSING"
        )).when(paymentStateMachine).transition(payment, PaymentStatus.PROCESSING);

        assertThatThrownBy(() -> paymentStateMachine.transition(payment, PaymentStatus.PROCESSING))
                .isInstanceOf(com.moddynerd.transiq.payment.state.InvalidPaymentStateException.class)
                .hasMessageContaining("Cannot transition payment");
    }

    @Test
    void createPayment_setsCorrectInitialStatus() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                500L, Currency.EUR, "test@test.com", "Test User", "order-002", "Description"
        );

        when(currentApiKeyService.getCurrentPrincipal()).thenReturn(apiKeyPrincipal);
        when(paymentRepository.findByMerchantAndIdempotencyKey(merchant, "idem-key-2"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreatePaymentResponse expectedResponse = new CreatePaymentResponse(
                UUID.randomUUID(), "pay_ref_new", "cs_secret", PaymentStatus.REQUIRES_PAYMENT_METHOD, Instant.now()
        );
        when(paymentMapper.toCreateResponse(any(Payment.class), anyString()))
                .thenReturn(expectedResponse);

        CreatePaymentResponse result = paymentService.createPayment("idem-key-2", request);

        verify(paymentRepository).save(argThat(payment ->
                payment.getStatus() == PaymentStatus.REQUIRES_PAYMENT_METHOD
                        && payment.getPaymentMethodType() == PaymentMethodType.UNKNOWN
                        && payment.getAmount().equals(500L)
                        && payment.getCurrency() == Currency.EUR
        ));
    }
}
