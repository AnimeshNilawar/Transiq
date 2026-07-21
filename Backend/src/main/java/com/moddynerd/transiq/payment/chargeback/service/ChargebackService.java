package com.moddynerd.transiq.payment.chargeback.service;

import com.moddynerd.transiq.payment.chargeback.dto.ChargebackResponse;
import com.moddynerd.transiq.payment.chargeback.dto.CreateChargebackRequest;

import java.util.List;

public interface ChargebackService {

    ChargebackResponse createChargeback(
            String idempotencyKey,
            CreateChargebackRequest request
    );

    ChargebackResponse getChargeback(String chargebackReference);

    List<ChargebackResponse> getChargebacks();
}
