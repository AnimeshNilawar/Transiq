package com.moddynerd.transiq.payment.financialEvent.service;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;
import com.moddynerd.transiq.payment.financialEvent.repository.FinancialEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialEventServiceImpl
        implements FinancialEventService {

    private final FinancialEventRepository repository;

    @Override
    public FinancialEvent create(
            Merchant merchant,
            FinancialEventType type,
            String reference,
            String description
    ) {

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