package com.moddynerd.transiq.webhook.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWebhookRequest(

        @NotBlank
        String url

) {
}