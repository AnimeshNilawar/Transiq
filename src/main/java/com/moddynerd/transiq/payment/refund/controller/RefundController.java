package com.moddynerd.transiq.payment.refund.controller;

import com.moddynerd.transiq.payment.refund.dto.CreateRefundRequest;
import com.moddynerd.transiq.payment.refund.dto.CreateRefundResponse;
import com.moddynerd.transiq.payment.refund.dto.RefundResponse;
import com.moddynerd.transiq.payment.refund.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/{paymentReference}")
    public ResponseEntity<CreateRefundResponse> createRefund(
            @PathVariable String paymentReference,

            @RequestHeader("Idempotency-Key")
            String idempotencyKey,

            @Valid
            @RequestBody
            CreateRefundRequest request
    ) {

        return ResponseEntity.ok(
                refundService.createRefund(
                        paymentReference,
                        idempotencyKey,
                        request
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<RefundResponse>> getRefunds() {

        return ResponseEntity.ok(
                refundService.getRefunds()
        );
    }

    @GetMapping("/{refundReference}")
    public ResponseEntity<RefundResponse> getRefund(
            @PathVariable String refundReference
    ) {

        return ResponseEntity.ok(
                refundService.getRefund(refundReference)
        );
    }

}