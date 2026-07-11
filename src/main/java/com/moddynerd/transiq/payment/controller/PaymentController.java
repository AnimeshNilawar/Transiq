package com.moddynerd.transiq.payment.controller;

import com.moddynerd.transiq.payment.dto.ConfirmPaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentRequest;
import com.moddynerd.transiq.payment.dto.CreatePaymentResponse;
import com.moddynerd.transiq.payment.dto.PaymentResponse;
import com.moddynerd.transiq.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(

            @RequestHeader("Idempotency-Key")
            String idempotencyKey,

            @Valid
            @RequestBody
            CreatePaymentRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        paymentService.createPayment(
                                idempotencyKey,
                                request
                        )
                );
    }

    @GetMapping("/{paymentReference}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable String paymentReference
    ) {

        return ResponseEntity.ok(
                paymentService.getPayment(paymentReference)
        );
    }

    @PostMapping("/{paymentReference}/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(

            @PathVariable
            String paymentReference,

            @Valid
            @RequestBody
            ConfirmPaymentRequest request
    ) {

        return ResponseEntity.ok(
                paymentService.confirmPayment(
                        paymentReference,
                        request
                )
        );
    }

    @PostMapping("/{paymentReference}/retry")
    public ResponseEntity<PaymentResponse> retryPayment(
            @PathVariable String paymentReference
    ) {

        return ResponseEntity.ok(
                paymentService.retryPayment(paymentReference)
        );
    }

}