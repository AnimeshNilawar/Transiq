package com.moddynerd.transiq.payment.settlement.controller;

import com.moddynerd.transiq.payment.settlement.dto.CreateSettlementResponse;
import com.moddynerd.transiq.payment.settlement.dto.SettlementResponse;
import com.moddynerd.transiq.payment.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping
    public ResponseEntity<CreateSettlementResponse> createSettlement() {

        return ResponseEntity.ok(
                settlementService.createSettlement()
        );
    }

    @GetMapping
    public ResponseEntity<List<SettlementResponse>> getSettlements() {

        return ResponseEntity.ok(
                settlementService.getSettlements()
        );
    }

    @GetMapping("/{settlementReference}")
    public ResponseEntity<SettlementResponse> getSettlement(
            @PathVariable String settlementReference
    ) {

        return ResponseEntity.ok(
                settlementService.getSettlement(settlementReference)
        );
    }
}