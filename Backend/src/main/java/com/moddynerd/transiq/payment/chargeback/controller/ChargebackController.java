package com.moddynerd.transiq.payment.chargeback.controller;

import com.moddynerd.transiq.payment.chargeback.dto.ChargebackResponse;
import com.moddynerd.transiq.payment.chargeback.dto.CreateChargebackRequest;
import com.moddynerd.transiq.payment.chargeback.service.ChargebackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chargebacks")
@RequiredArgsConstructor
public class ChargebackController {

    private final ChargebackService chargebackService;

    @PostMapping
    public ResponseEntity<ChargebackResponse> createChargeback(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateChargebackRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chargebackService.createChargeback(idempotencyKey, request));
    }

    @GetMapping
    public ResponseEntity<List<ChargebackResponse>> getChargebacks() {
        return ResponseEntity.ok(chargebackService.getChargebacks());
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ChargebackResponse> getChargeback(
            @PathVariable String reference
    ) {
        return ResponseEntity.ok(chargebackService.getChargeback(reference));
    }
}
