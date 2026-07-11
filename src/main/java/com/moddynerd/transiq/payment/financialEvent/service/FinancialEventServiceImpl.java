package com.moddynerd.transiq.payment.financialEvent.service;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
import com.moddynerd.transiq.payment.financialEvent.repository.FinancialEventRepository;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialEventServiceImpl
        implements FinancialEventService {

    private final FinancialEventRepository repository;
    private final CurrentApiKeyService currentApiKeyService;

    @Override
    public FinancialEvent create(
            FinancialEventType type,
            String reference,
            String description
    ) {

        Merchant merchant =
                currentApiKeyService
                        .getCurrentPrincipal()
                        .merchant();

        FinancialEvent event =
                FinancialEvent.builder()
                        .merchant(merchant)
                        .type(type)
                        .reference(reference)
                        .description(description)
                        .build();

        return repository.save(event);
    }
}