package com.moddynerd.transiq.webhook.mapper;

import com.moddynerd.transiq.webhook.dto.CreateWebhookResponse;
import com.moddynerd.transiq.webhook.dto.WebhookResponse;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import org.springframework.stereotype.Component;

@Component
public class WebhookMapper {

    public CreateWebhookResponse toCreateResponse(
            WebhookEndpoint endpoint,
            String secret
    ){
        return new CreateWebhookResponse(
                endpoint.getId(),
                endpoint.getUrl(),
                secret
        );
    }

    public WebhookResponse toResponse(
            WebhookEndpoint endpoint
    ) {
        return new WebhookResponse(
                endpoint.getId(),
                endpoint.getUrl(),
                endpoint.getStatus()
        );
    }
}
