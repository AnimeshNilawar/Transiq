package com.moddynerd.transiq.merchant.controller;

import com.moddynerd.transiq.merchant.dto.MerchantRegistrationRequest;
import com.moddynerd.transiq.merchant.dto.MerchantResponse;
import com.moddynerd.transiq.merchant.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantResponse registerMerchant(
            @Valid @RequestBody MerchantRegistrationRequest request
            ) {
        return merchantService.registerMerchant(request);
    }

}
