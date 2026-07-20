package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.webhook.dto.CreateWebhookRequest;
import com.moddynerd.transiq.webhook.dto.CreateWebhookResponse;
import com.moddynerd.transiq.webhook.dto.WebhookResponse;

import java.util.List;
import java.util.UUID;

public interface WebhookService {

    CreateWebhookResponse createWebhook(CreateWebhookRequest request);

    CreateWebhookResponse createWebhook(Merchant merchant, CreateWebhookRequest request);

    List<WebhookResponse> getWebhooks();

    void disableWebhook(UUID webhookId);

    void disableWebhook(Merchant merchant, UUID webhookId);
}
