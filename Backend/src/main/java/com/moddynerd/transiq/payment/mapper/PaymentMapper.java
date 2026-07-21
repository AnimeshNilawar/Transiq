package com.moddynerd.transiq.payment.mapper;

import com.moddynerd.transiq.payment.dto.CreatePaymentResponse;
import com.moddynerd.transiq.payment.dto.PaymentResponse;
import com.moddynerd.transiq.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public CreatePaymentResponse toCreateResponse(
            Payment payment,
            String clientSecret
    ) {

        return new CreatePaymentResponse(
                payment.getId(),
                payment.getPaymentReference(),
                clientSecret,
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    public PaymentResponse toResponse(
            Payment payment
    ) {

        PaymentResponse.UpiPaymentDetailResponse upiDetails = null;
        if (payment.getUpiPaymentDetails() != null) {
            upiDetails = new PaymentResponse.UpiPaymentDetailResponse(
                    payment.getUpiPaymentDetails().getUpiId(),
                    payment.getUpiPaymentDetails().getUpiTransactionReference()
            );
        }

        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentReference(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCustomerEmail(),
                payment.getOrderId(),
                payment.getCreatedAt(),
                payment.getPaymentMethodType(),
                upiDetails
        );
    }

}