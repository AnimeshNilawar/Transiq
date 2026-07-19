package com.moddynerd.transiq.webhook.controller;

import com.moddynerd.transiq.webhook.dto.CreateWebhookRequest;
import com.moddynerd.transiq.webhook.dto.CreateWebhookResponse;
import com.moddynerd.transiq.webhook.dto.WebhookResponse;
import com.moddynerd.transiq.webhook.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping
    public ResponseEntity<CreateWebhookResponse> createWebhook(
            @Valid
            @RequestBody
            CreateWebhookRequest request
    ) {

        return ResponseEntity.ok(
                webhookService.createWebhook(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<WebhookResponse>> getWebhooks() {

        return ResponseEntity.ok(
                webhookService.getWebhooks()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disableWebhook(
            @PathVariable UUID id
    ) {

        webhookService.disableWebhook(id);

        return ResponseEntity.noContent().build();
    }
}