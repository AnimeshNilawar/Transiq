package com.moddynerd.transiq.webhook.dispatcher;

import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import com.moddynerd.transiq.webhook.entity.*;
import com.moddynerd.transiq.webhook.repository.WebhookEndpointRepository;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HttpWebhookDispatcher
        implements WebhookDispatcher {

    private final RestClient restClient;
    private final WebhookEndpointRepository repository;
    private final WebhookDeliveryService deliveryService;

    @Override
    public void dispatch(WebhookPayload payload, UUID merchantId) {
        List<WebhookEndpoint> endpoints = repository.findAllByMerchant_IdAndStatus(merchantId, WebhookStatus.ACTIVE);

        for (WebhookEndpoint endpoint : endpoints) {

            WebhookDelivery delivery = deliveryService.createDelivery(endpoint, payload);

            long start = System.currentTimeMillis();

            try {

                ResponseEntity<Void> response =
                        restClient.post()
                                .uri(endpoint.getUrl())
                                .body(payload)
                                .retrieve()
                                .toBodilessEntity();

                deliveryService.markDelivered(
                        delivery,
                        response,
                        System.currentTimeMillis() - start
                );

            } catch (Exception ex) {

                deliveryService.markFailed(
                        delivery,
                        ex,
                        System.currentTimeMillis() - start
                );

            }

        }
    }



}

