package com.moddynerd.transiq.payment.service;

import com.moddynerd.transiq.apikey.security.ApiKeyPrincipal;
import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.dto.ConfirmPaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentResponse;
import com.moddynerd.transiq.payment.dto.PaymentResponse;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.mapper.PaymentMapper;
import com.moddynerd.transiq.payment.processor.PaymentProcessor;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.payment.state.PaymentStateMachine;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import com.moddynerd.transiq.shared.util.ClientSecretGenerator;
import com.moddynerd.transiq.shared.util.PaymentReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        payment.setPaymentMethodType(request.paymentMethodType());

        paymentRepository.save(payment);

        paymentProcessor.process(payment);

        return paymentMapper.toResponse(payment);
    }

}