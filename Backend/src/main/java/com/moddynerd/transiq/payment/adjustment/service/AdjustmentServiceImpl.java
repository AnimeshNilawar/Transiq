package com.moddynerd.transiq.payment.adjustment.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.adjustment.dto.AdjustmentResponse;
import com.moddynerd.transiq.payment.adjustment.dto.CreateAdjustmentRequest;
import com.moddynerd.transiq.payment.adjustment.entity.Adjustment;
import com.moddynerd.transiq.payment.adjustment.entity.AdjustmentType;
import com.moddynerd.transiq.payment.adjustment.mapper.AdjustmentMapper;
import com.moddynerd.transiq.payment.adjustment.repository.AdjustmentRepository;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
import com.moddynerd.transiq.payment.financialEvent.repository.FinancialEventRepository;
import com.moddynerd.transiq.payment.ledger.entity.EntrySide;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntryType;
import com.moddynerd.transiq.payment.ledger.repository.LedgerEntryRepository;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdjustmentServiceImpl implements AdjustmentService {

    private final AdjustmentRepository adjustmentRepository;
    private final AdjustmentMapper adjustmentMapper;
    private final FinancialEventRepository financialEventRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final CurrentApiKeyService currentApiKeyService;

    @Override
    public AdjustmentResponse createAdjustment(CreateAdjustmentRequest request) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        String reference;
        do {
            reference = generateAdjustmentReference();
        } while (adjustmentRepository.findByAdjustmentReference(reference).isPresent());

        Adjustment adjustment = Adjustment.builder()
                .merchant(merchant)
                .adjustmentReference(reference)
                .amount(request.amount())
                .type(request.type())
                .reason(request.reason())
                .createdBy(merchant.getBusinessEmail())
                .build();

        adjustmentRepository.save(adjustment);

        FinancialEvent financialEvent = FinancialEvent.builder()
                .merchant(merchant)
                .type(FinancialEventType.ADJUSTMENT)
                .reference(reference)
                .description("Manual ledger adjustment: " + request.type().name())
                .build();

        financialEventRepository.save(financialEvent);

        if (request.type() == AdjustmentType.CREDIT) {
            createLedgerEntry(
                    financialEvent, merchant, LedgerEntryType.ADJUSTMENT,
                    LedgerAccount.MERCHANT_PAYABLE, EntrySide.CREDIT,
                    request.amount(), "Manual credit adjustment"
            );
        } else {
            createLedgerEntry(
                    financialEvent, merchant, LedgerEntryType.ADJUSTMENT,
                    LedgerAccount.MERCHANT_PAYABLE, EntrySide.DEBIT,
                    request.amount(), "Manual debit adjustment"
            );
        }

        return adjustmentMapper.toResponse(adjustment);
    }

    @Override
    @Transactional(readOnly = true)
    public AdjustmentResponse getAdjustment(String adjustmentReference) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        Adjustment adjustment = adjustmentRepository.findByAdjustmentReference(adjustmentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Adjustment not found"));

        if (!adjustment.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Adjustment not found");
        }

        return adjustmentMapper.toResponse(adjustment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdjustmentResponse> getAdjustments() {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        return adjustmentRepository.findAllByMerchantOrderByCreatedAtDesc(merchant)
                .stream()
                .map(adjustmentMapper::toResponse)
                .toList();
    }

    private void createLedgerEntry(
            FinancialEvent event,
            Merchant merchant,
            LedgerEntryType entryType,
            LedgerAccount account,
            EntrySide side,
            Long amount,
            String description
    ) {
        LedgerEntry entry = LedgerEntry.builder()
                .merchant(merchant)
                .financialEvent(event)
                .entryType(entryType)
                .account(account)
                .side(side)
                .amount(amount)
                .description(description)
                .build();

        ledgerEntryRepository.save(entry);
    }

    private String generateAdjustmentReference() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder("adj_");
        for (int i = 0; i < 16; i++) {
            builder.append(chars.charAt(
                    new SecureRandom().nextInt(chars.length())
            ));
        }
        return builder.toString();
    }
}
