package com.moddynerd.transiq.payment.financialEvent.service;

import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEventType;

public interface FinancialEventService {

    FinancialEvent create(
            FinancialEventType type,
            String reference,
            String description
    );

}