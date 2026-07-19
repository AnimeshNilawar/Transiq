package com.moddynerd.transiq.webhook.controller;

import com.moddynerd.transiq.webhook.dto.request.WebhookDeliveryFilter;
import com.moddynerd.transiq.webhook.dto.response.WebhookDeliveryResponse;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEventType;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryQueryService;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryRetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhooks/deliveries")
@RequiredArgsConstructor
public class WebhookDeliveryController {

    private final WebhookDeliveryQueryService queryService;
    private final WebhookDeliveryRetryService retryService;

    @GetMapping
    public Page<WebhookDeliveryResponse> getDeliveries(

            WebhookDeliveryStatus status,
            WebhookEventType eventType,
            UUID endpointId,
            UUID eventId,
            Instant from,
            Instant to,
            Pageable pageable
    ) {

        WebhookDeliveryFilter filter = new WebhookDeliveryFilter(
                        status,
                        eventType,
                        endpointId,
                        eventId,
                        from,
                        to
                );

        return queryService.getDeliveries(filter, pageable);
    }

    @GetMapping("/{id}")
    public WebhookDeliveryResponse getDelivery(@PathVariable UUID id) {

        return queryService.getDelivery(id);

    }

    @PostMapping("/{id}/retry")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void retryDelivery(@PathVariable UUID id) {

        retryService.retry(id);

    }

}