package com.moddynerd.transiq.payment.adjustment.controller;

import com.moddynerd.transiq.payment.adjustment.dto.AdjustmentResponse;
import com.moddynerd.transiq.payment.adjustment.dto.CreateAdjustmentRequest;
import com.moddynerd.transiq.payment.adjustment.service.AdjustmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/adjustments")
@RequiredArgsConstructor
public class AdjustmentController {

    private final AdjustmentService adjustmentService;

    @PostMapping
    public ResponseEntity<AdjustmentResponse> createAdjustment(
            @Valid @RequestBody CreateAdjustmentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adjustmentService.createAdjustment(request));
    }

    @GetMapping
    public ResponseEntity<List<AdjustmentResponse>> getAdjustments() {
        return ResponseEntity.ok(adjustmentService.getAdjustments());
    }

    @GetMapping("/{reference}")
    public ResponseEntity<AdjustmentResponse> getAdjustment(
            @PathVariable String reference
    ) {
        return ResponseEntity.ok(adjustmentService.getAdjustment(reference));
    }
}
