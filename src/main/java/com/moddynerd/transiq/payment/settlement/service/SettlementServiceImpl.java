package com.moddynerd.transiq.payment.settlement.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.event.publisher.DomainEventPublisher;
import com.moddynerd.transiq.event.settlement.SettlementCompletedEvent;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.ledger.dto.MerchantBalanceResponse;
import com.moddynerd.transiq.payment.ledger.service.MerchantBalanceService;
import com.moddynerd.transiq.payment.settlement.dto.CreateSettlementResponse;
import com.moddynerd.transiq.payment.settlement.dto.SettlementResponse;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;
import com.moddynerd.transiq.payment.settlement.entity.SettlementStatus;
import com.moddynerd.transiq.payment.settlement.mapper.SettlementMapper;
import com.moddynerd.transiq.payment.settlement.repository.SettlementRepository;
import com.moddynerd.transiq.shared.exception.ConflictException;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import com.moddynerd.transiq.shared.util.SettlementReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementServiceImpl implements SettlementService{

    private final SettlementRepository settlementRepository;
    private final SettlementMapper settlementMapper;
    private final MerchantBalanceService merchantBalanceService;
    private final CurrentApiKeyService currentApiKeyService;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public CreateSettlementResponse createSettlement() {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        MerchantBalanceResponse balance = merchantBalanceService.getBalance();

        if(balance.availableBalance() <= 0){
            throw new ConflictException("No funds available for settlement");
        }

        String reference;

        do{
            reference = SettlementReferenceGenerator.generate();
        } while (settlementRepository
                .findBySettlementReference(reference)
                .isPresent());

        Settlement settlement = Settlement.builder()
                .merchant(merchant)
                .settlementReference(reference)
                .amount(balance.availableBalance())
                .currency(balance.currency())
                .status(SettlementStatus.PENDING)
                .build();

        settlementRepository.save(settlement);

        domainEventPublisher.publish(
                new SettlementCompletedEvent(
                        settlement.getId(),
                        settlement.getSettlementReference()
                )
        );

        settlement.setStatus(SettlementStatus.COMPLETED);
        settlement.setProcessedAt(Instant.now());
        settlement.setBankReference(
                "BANK_" + UUID.randomUUID()
                        .toString()
                        .substring(0,8)
                        .toUpperCase()
        );

        settlementRepository.save(settlement);

        return settlementMapper.toCreateResponse(settlement);


    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlements() {

        Merchant merchant =
                currentApiKeyService.getCurrentPrincipal().merchant();

        return settlementRepository
                .findAllByMerchantOrderByCreatedAtDesc(merchant)
                .stream()
                .map(settlementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SettlementResponse getSettlement(
            String settlementReference
    ) {

        Merchant merchant =
                currentApiKeyService.getCurrentPrincipal().merchant();

        Settlement settlement = settlementRepository
                .findBySettlementReference(settlementReference)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Settlement not found"
                        ));

        if (!settlement.getMerchant().getId().equals(merchant.getId())) {
            throw new ResourceNotFoundException(
                    "Settlement not found"
            );
        }

        return settlementMapper.toResponse(settlement);
    }
}
