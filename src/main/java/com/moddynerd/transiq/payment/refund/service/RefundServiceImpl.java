package com.moddynerd.transiq.payment.refund.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
import com.moddynerd.transiq.payment.financialEvent.service.FinancialEventService;
import com.moddynerd.transiq.payment.ledger.service.LedgerService;
import com.moddynerd.transiq.payment.refund.dto.CreateRefundRequest;
import com.moddynerd.transiq.payment.refund.dto.CreateRefundResponse;
import com.moddynerd.transiq.payment.refund.dto.RefundResponse;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import com.moddynerd.transiq.payment.refund.entity.RefundStatus;
import com.moddynerd.transiq.payment.refund.mapper.RefundMapper;
import com.moddynerd.transiq.payment.refund.repository.RefundRepository;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.shared.exception.ConflictException;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import com.moddynerd.transiq.shared.util.RefundReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundServiceImpl implements RefundService{

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final RefundMapper refundMapper;
    private final CurrentApiKeyService currentApiKeyService;
    private final FinancialEventService financialEventService;
    private final LedgerService ledgerService;

    @Override
    public CreateRefundResponse createRefund(String paymentReference, String idempotencyKey, CreateRefundRequest request) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        Payment payment = paymentRepository.findByMerchantAndPaymentReference(merchant, paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if(payment.getStatus() != PaymentStatus.SUCCEEDED){
            throw new ConflictException("Only successful payments can be refunded");
        }

        Optional<Refund> existingRefund = refundRepository.findByPaymentAndIdempotencyKey(payment, idempotencyKey);

        if(existingRefund.isPresent()){
            return refundMapper.toCreateResponse(
                    existingRefund.get()
            );
        }

        Long remaining =
                payment.getAmount() - payment.getRefundedAmount();

        if (request.amount() > remaining) {
            throw new ConflictException(
                    "Refund amount exceeds remaining refundable balance."
            );
        }

        if(request.amount().compareTo(remaining) > 0){
            throw new ConflictException("Refund amount exceeds remaining refundable balance.");
        }

        // Generate Refund reference
        String reference;

        do{
            reference = RefundReferenceGenerator.generate();
        } while (
                refundRepository
                        .findByRefundReference(reference)
                        .isPresent()
        );

        //Create Refund
        Refund refund = Refund.builder()
                .payment(payment)
                .merchant(merchant)
                .refundReference(reference)
                .amount(request.amount())
                .reason(request.reason())
                .status(RefundStatus.SUCCEEDED)
                .idempotencyKey(idempotencyKey)
                .build();

        refundRepository.save(refund);

        // Financial Event
        FinancialEvent event = financialEventService.create(
                FinancialEventType.REFUND,
                refund.getRefundReference(),
                "Refund issued"
        );

        // Ledger
        ledgerService.recordRefund(event, refund);

        // Update payment
        payment.setRefundedAmount( payment.getRefundedAmount() + refund.getAmount() );

        if(payment.getRefundedAmount().compareTo(payment.getAmount()) == 0){
            payment.setStatus(PaymentStatus.REFUNDED);
        }

        paymentRepository.save(payment);

        return refundMapper.toCreateResponse(
                refund
        );
    }

    @Override
    public RefundResponse getRefund(String refundReference) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        Refund refund = refundRepository.findByRefundReference(refundReference)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));

        if(!refund.getMerchant().getId().equals(merchant.getId())){
            throw new ResourceNotFoundException("Refund not found");
        }

        return refundMapper.toResponse(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> getRefunds() {

        Merchant merchant =
                currentApiKeyService.getCurrentPrincipal().merchant();

        return refundRepository
                .findAllByMerchantOrderByCreatedAtDesc(merchant)
                .stream()
                .map(refundMapper::toResponse)
                .toList();
    }
}
