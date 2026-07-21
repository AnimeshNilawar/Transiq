package com.moddynerd.transiq.payment.chargeback.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.event.chargeback.ChargebackCreatedEvent;
import com.moddynerd.transiq.event.publisher.DomainEventPublisher;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.chargeback.dto.ChargebackResponse;
import com.moddynerd.transiq.payment.chargeback.dto.CreateChargebackRequest;
import com.moddynerd.transiq.payment.chargeback.entity.Chargeback;
import com.moddynerd.transiq.payment.chargeback.entity.ChargebackStatus;
import com.moddynerd.transiq.payment.chargeback.mapper.ChargebackMapper;
import com.moddynerd.transiq.payment.chargeback.repository.ChargebackRepository;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.shared.exception.ConflictException;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChargebackServiceImpl implements ChargebackService {

    private final ChargebackRepository chargebackRepository;
    private final PaymentRepository paymentRepository;
    private final ChargebackMapper chargebackMapper;
    private final CurrentApiKeyService currentApiKeyService;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public ChargebackResponse createChargeback(
            String idempotencyKey,
            CreateChargebackRequest request
    ) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        Payment payment = paymentRepository.findByMerchantAndPaymentReference(merchant, request.paymentReference())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new ConflictException("Only successful payments can be charged back");
        }

        if (request.amount() > payment.getAmount()) {
            throw new ConflictException("Chargeback amount exceeds payment amount");
        }

        String reference;
        do {
            reference = generateChargebackReference();
        } while (chargebackRepository.findByChargebackReference(reference).isPresent());

        Chargeback chargeback = Chargeback.builder()
                .payment(payment)
                .merchant(merchant)
                .chargebackReference(reference)
                .amount(request.amount())
                .status(ChargebackStatus.PENDING)
                .reason(request.reason())
                .evidence(request.evidence())
                .build();

        chargebackRepository.save(chargeback);

        domainEventPublisher.publish(
                new ChargebackCreatedEvent(
                        merchant.getId(),
                        chargeback.getId(),
                        chargeback.getChargebackReference()
                )
        );

        return chargebackMapper.toResponse(chargeback);
    }

    @Override
    @Transactional(readOnly = true)
    public ChargebackResponse getChargeback(String chargebackReference) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        Chargeback chargeback = chargebackRepository.findByChargebackReference(chargebackReference)
                .orElseThrow(() -> new ResourceNotFoundException("Chargeback not found"));

        if (!chargeback.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Chargeback not found");
        }

        return chargebackMapper.toResponse(chargeback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargebackResponse> getChargebacks() {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        return chargebackRepository.findAllByMerchantOrderByCreatedAtDesc(merchant)
                .stream()
                .map(chargebackMapper::toResponse)
                .toList();
    }

    private String generateChargebackReference() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder("cb_");
        for (int i = 0; i < 16; i++) {
            builder.append(chars.charAt(
                    new java.security.SecureRandom().nextInt(chars.length())
            ));
        }
        return builder.toString();
    }
}
