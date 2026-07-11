package com.moddynerd.transiq.payment.refund.dto;

public record CreateRefundResponse(

        String refundReference,

        Long amount,

        String status

) {
}