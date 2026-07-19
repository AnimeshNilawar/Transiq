package com.moddynerd.transiq.webhook.dto;

import com.moddynerd.transiq.webhook.entity.WebhookStatus;

import java.util.UUID;

public record WebhookResponse(

        UUID id,

        String url,

        WebhookStatus status

) {
}