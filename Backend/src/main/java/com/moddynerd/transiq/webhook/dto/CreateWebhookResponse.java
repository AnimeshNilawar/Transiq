package com.moddynerd.transiq.webhook.dto;

import java.util.UUID;

public record CreateWebhookResponse(

        UUID id,

        String url,

        String secret

) {
}