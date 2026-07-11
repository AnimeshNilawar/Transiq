package com.moddynerd.transiq.payment.refund.service;

import com.moddynerd.transiq.payment.refund.dto.CreateRefundRequest;
import com.moddynerd.transiq.payment.refund.dto.CreateRefundResponse;
import com.moddynerd.transiq.payment.refund.dto.RefundResponse;

import java.util.List;

public interface RefundService {

    CreateRefundResponse createRefund(
            String paymentReference,
            String idempotencyKey,
            CreateRefundRequest request
    );

    RefundResponse getRefund(
            String refundReference
    );

    List<RefundResponse> getRefunds();

}