package com.moddynerd.transiq.payment.chargeback.mapper;

import com.moddynerd.transiq.payment.chargeback.dto.ChargebackResponse;
import com.moddynerd.transiq.payment.chargeback.entity.Chargeback;
import org.springframework.stereotype.Component;

@Component
public class ChargebackMapper {

    public ChargebackResponse toResponse(Chargeback chargeback) {
        return new ChargebackResponse(
                chargeback.getChargebackReference(),
                chargeback.getPayment().getPaymentReference(),
                chargeback.getAmount(),
                chargeback.getStatus(),
                chargeback.getReason(),
                chargeback.getEvidence(),
                chargeback.getCreatedAt()
        );
    }
}
