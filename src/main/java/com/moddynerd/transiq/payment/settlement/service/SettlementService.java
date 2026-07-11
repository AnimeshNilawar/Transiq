package com.moddynerd.transiq.payment.settlement.service;

import com.moddynerd.transiq.payment.settlement.dto.CreateSettlementResponse;
import com.moddynerd.transiq.payment.settlement.dto.SettlementResponse;

import java.util.List;

public interface SettlementService {

    CreateSettlementResponse createSettlement();

    List<SettlementResponse> getSettlements();

    SettlementResponse getSettlement(
            String settlementReference
    );

}