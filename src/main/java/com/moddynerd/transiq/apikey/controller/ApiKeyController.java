package com.moddynerd.transiq.apikey.controller;

import com.moddynerd.transiq.apikey.dto.ApiKeyResponse;
import com.moddynerd.transiq.apikey.dto.CreateApiKeyRequest;
import com.moddynerd.transiq.apikey.dto.CreateApiKeyResponse;
import com.moddynerd.transiq.apikey.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<CreateApiKeyResponse> createApiKey(
            @Valid @RequestBody CreateApiKeyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.createApiKey(request));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> getApiKeys() {
        return ResponseEntity.ok(apiKeyService.getApiKeys());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revokeApiKey(
            @PathVariable UUID id
    ) {

        apiKeyService.revokeApiKey(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/rotate")
    public ResponseEntity<CreateApiKeyResponse> rotateApiKey(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                apiKeyService.rotateApiKey(id)
        );
    }
}