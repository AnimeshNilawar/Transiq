package com.moddynerd.transiq.payment.settlement.mapper;

import com.moddynerd.transiq.payment.settlement.dto.CreateSettlementResponse;
import com.moddynerd.transiq.payment.settlement.dto.SettlementResponse;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;
import org.springframework.stereotype.Component;

@Component
public class SettlementMapper {

    public CreateSettlementResponse toCreateResponse(
            Settlement settlement
    ) {

        return new CreateSettlementResponse(
                settlement.getSettlementReference(),
                settlement.getAmount(),
                settlement.getCurrency(),
                settlement.getStatus().name(),
                settlement.getCreatedAt()
        );
    }

    public SettlementResponse toResponse(
            Settlement settlement
    ) {

        return new SettlementResponse(
                settlement.getSettlementReference(),
                settlement.getAmount(),
                settlement.getCurrency(),
                settlement.getStatus().name(),
                settlement.getProcessedAt(),
                settlement.getBankReference()
        );
    }
}