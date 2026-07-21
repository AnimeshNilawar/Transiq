package com.moddynerd.transiq.payment.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.dto.ConfirmPaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentResponse;
import com.moddynerd.transiq.payment.dto.PaymentResponse;
import com.moddynerd.transiq.payment.entity.CardPaymentDetails;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.entity.UpiPaymentDetails;
import com.moddynerd.transiq.payment.expiration.PaymentExpirationService;
import com.moddynerd.transiq.payment.mapper.PaymentMapper;
import com.moddynerd.transiq.payment.processor.PaymentProcessor;
import com.moddynerd.transiq.payment.repository.CardPaymentDetailsRepository;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.payment.repository.UpiPaymentDetailsRepository;
import com.moddynerd.transiq.payment.security.ClientSecretService;
import com.moddynerd.transiq.payment.state.PaymentStateMachine;
import com.moddynerd.transiq.shared.exception.ConflictException;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import com.moddynerd.transiq.shared.util.ClientSecretGenerator;
import com.moddynerd.transiq.shared.util.PaymentReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentApiKeyService currentApiKeyService;
    private final PaymentProcessor paymentProcessor;
    private final PaymentStateMachine paymentStateMachine;
    private final ClientSecretService clientSecretService;
    private final PaymentExpirationService paymentExpirationService;
    private final CardPaymentDetailsRepository cardPaymentDetailsRepository;
    private final UpiPaymentDetailsRepository upiPaymentDetailsRepository;

    @Override
    public CreatePaymentResponse createPayment(
            String idempotencyKey,
            CreatePaymentRequest request
    ) {

        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        Optional<Payment> existingPayment =
                paymentRepository.findByMerchantAndIdempotencyKey(
                        merchant,
                        idempotencyKey
                );

        if (existingPayment.isPresent()) {

            return paymentMapper.toCreateResponse(
                    existingPayment.get(),
                    null
            );
        }

        String paymentReference;

        do {
            paymentReference = PaymentReferenceGenerator.generate();
        } while (paymentRepository.findByPaymentReference(paymentReference).isPresent());

        String clientSecret =
                ClientSecretGenerator.generate(paymentReference);

        String clientSecretHash =
                passwordEncoder.encode(clientSecret);

        Payment payment = Payment.builder()
                .merchant(merchant)
                .paymentReference(paymentReference)
                .amount(request.amount())
                .currency(request.currency())
                .paymentMethodType(PaymentMethodType.UNKNOWN)
                .status(PaymentStatus.REQUIRES_PAYMENT_METHOD)
                .idempotencyKey(idempotencyKey)
                .customerEmail(request.customerEmail())
                .customerName(request.customerName())
                .orderId(request.orderId())
                .description(request.description())
                .clientSecretHash(clientSecretHash)
                .expiresAt(Instant.now().plusSeconds(15*60))
                .build();

        paymentRepository.save(payment);

        return paymentMapper.toCreateResponse(
                payment,
                clientSecret
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentReference) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        Payment payment = paymentRepository.findByMerchantAndPaymentReference(
                merchant,
                paymentReference
        ).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse confirmPayment(
            String paymentReference,
            ConfirmPaymentRequest request
    ) {

        Merchant merchant =
                currentApiKeyService.getCurrentPrincipal().merchant();

        Payment payment = paymentRepository
                .findByMerchantAndPaymentReference(
                        merchant,
                        paymentReference
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        paymentExpirationService.validate(payment);

        clientSecretService.verify(
                payment,
                request.clientSecret()
        );

        payment.setPaymentMethodType(request.paymentMethodType());

        paymentRepository.save(payment);

        if (request.paymentMethodType() == PaymentMethodType.CARD
                && request.cardNetwork() != null
                && request.issuerBank() != null) {

            CardPaymentDetails details = CardPaymentDetails.builder()
                    .payment(payment)
                    .cardNetwork(request.cardNetwork())
                    .issuerBank(request.issuerBank())
                    .maskedCardNumber(request.maskedCardNumber())
                    .expiryMonth(request.expiryMonth())
                    .expiryYear(request.expiryYear())
                    .build();

            cardPaymentDetailsRepository.save(details);
        }

        if (request.paymentMethodType() == PaymentMethodType.UPI
                && request.upiId() != null) {

            UpiPaymentDetails details = UpiPaymentDetails.builder()
                    .payment(payment)
                    .upiId(request.upiId())
                    .build();

            upiPaymentDetailsRepository.save(details);
        }

        paymentProcessor.process(payment);

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse retryPayment(String paymentReference) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();
        return retryPayment(merchant, paymentReference);
    }

    @Override
    public PaymentResponse retryPayment(Merchant merchant, String paymentReference) {
        Payment payment = paymentRepository.findByMerchantAndPaymentReference(
                merchant,
                paymentReference
        ).orElseThrow( () -> new ResourceNotFoundException("Payment not Found"));

        paymentExpirationService.validate(payment);

        if(payment.getStatus() != PaymentStatus.FAILED && payment.getStatus() != PaymentStatus.REQUIRES_PAYMENT_METHOD){
            throw  new ConflictException("Payment cannot be retried in current state");
        }

        if(payment.getStatus() == PaymentStatus.FAILED){
            paymentStateMachine.transition(
                    payment,
                    PaymentStatus.REQUIRES_PAYMENT_METHOD
            );
            paymentRepository.save(payment);
        }

        paymentProcessor.process(payment);

        return paymentMapper.toResponse(payment);
    }

}