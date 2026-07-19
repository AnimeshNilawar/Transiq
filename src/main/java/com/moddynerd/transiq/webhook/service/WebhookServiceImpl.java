package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.shared.security.CurrentApiKeyService;
import com.moddynerd.transiq.shared.util.WebhookSecretGenerator;
import com.moddynerd.transiq.webhook.dto.CreateWebhookRequest;
import com.moddynerd.transiq.webhook.dto.CreateWebhookResponse;
import com.moddynerd.transiq.webhook.dto.WebhookResponse;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.entity.WebhookStatus;
import com.moddynerd.transiq.webhook.mapper.WebhookMapper;
import com.moddynerd.transiq.webhook.repository.WebhookEndpointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final WebhookEndpointRepository repository;
    private final WebhookMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentApiKeyService currentApiKeyService;

    @Override
    public CreateWebhookResponse createWebhook(CreateWebhookRequest request) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        String secret = WebhookSecretGenerator.generate();
        String hash = passwordEncoder.encode(secret);

        WebhookEndpoint endpoint =
                WebhookEndpoint.builder()
                        .merchant(merchant)
                        .url(request.url())
                        .secretHash(hash)
                        .status(WebhookStatus.ACTIVE)
                        .version(1)
                        .build();

        repository.save(endpoint);

        return mapper.toCreateResponse(endpoint, secret);
    }

    @Override
    public List<WebhookResponse> getWebhooks() {
        Merchant merchant =
                currentApiKeyService.getCurrentPrincipal().merchant();

        return repository
                .findAllByMerchant(merchant)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void disableWebhook(UUID webhookId) {
        Merchant merchant = currentApiKeyService.getCurrentPrincipal().merchant();

        WebhookEndpoint endpoint = repository.findById(webhookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Webhook not found"
                        ));

        if(!endpoint.getMerchant().getId().equals(merchant.getId())){
            throw new ResourceNotFoundException("Webhook not found");
        }

        endpoint.setStatus(WebhookStatus.DISABLED);
        repository.save(endpoint);
    }
}
