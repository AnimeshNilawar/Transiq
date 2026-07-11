package com.moddynerd.transiq.payment.refund.mapper;

import com.moddynerd.transiq.payment.refund.dto.CreateRefundResponse;
import com.moddynerd.transiq.payment.refund.dto.RefundResponse;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import org.springframework.stereotype.Component;

@Component
public class RefundMapper {

    public CreateRefundResponse toCreateResponse(
            Refund refund
    ) {

        return new CreateRefundResponse(
                refund.getRefundReference(),
                refund.getAmount(),
                refund.getStatus().name()
        );
    }

    public RefundResponse toResponse(
            Refund refund
    ) {

        return new RefundResponse(
                refund.getRefundReference(),
                refund.getPayment().getPaymentReference(),
                refund.getAmount(),
                refund.getStatus(),
                refund.getReason(),
                refund.getCreatedAt()
        );
    }
}