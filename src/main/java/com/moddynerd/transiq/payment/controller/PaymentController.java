package com.moddynerd.transiq.payment.controller;

import com.moddynerd.transiq.apikey.security.ApiKeyPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @GetMapping("/test")
    public String test(Authentication authentication) {

        ApiKeyPrincipal principal =
                (ApiKeyPrincipal) authentication.getPrincipal();

        return """
                Merchant: %s
                Environment: %s
                Type: %s
                """
                .formatted(
                        principal.merchant().getBusinessName(),
                        principal.environment(),
                        principal.type()
                );
    }
}