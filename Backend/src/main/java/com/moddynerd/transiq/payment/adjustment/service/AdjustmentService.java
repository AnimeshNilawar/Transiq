package com.moddynerd.transiq.payment.adjustment.service;

import com.moddynerd.transiq.payment.adjustment.dto.AdjustmentResponse;
import com.moddynerd.transiq.payment.adjustment.dto.CreateAdjustmentRequest;

import java.util.List;

public interface AdjustmentService {

    AdjustmentResponse createAdjustment(CreateAdjustmentRequest request);

    AdjustmentResponse getAdjustment(String adjustmentReference);

    List<AdjustmentResponse> getAdjustments();
}
