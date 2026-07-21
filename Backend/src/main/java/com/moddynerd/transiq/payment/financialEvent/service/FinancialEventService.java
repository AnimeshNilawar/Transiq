package com.moddynerd.transiq.payment.financialEvent.service;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;

public interface FinancialEventService {

    FinancialEvent create(
            Merchant merchant,
            FinancialEventType type,
            String reference,
            String description
    );

}