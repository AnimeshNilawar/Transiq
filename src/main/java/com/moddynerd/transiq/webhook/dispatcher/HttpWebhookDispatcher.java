package com.moddynerd.transiq.webhook.dispatcher;

import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import com.moddynerd.transiq.webhook.entity.*;
import com.moddynerd.transiq.webhook.repository.WebhookEndpointRepository;
import com.moddynerd.transiq.webhook.sender.WebhookSender;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HttpWebhookDispatcher
        implements WebhookDispatcher {

    private final WebhookEndpointRepository repository;
    private final WebhookDeliveryService deliveryService;
    private final ObjectMapper objectMapper;
    private final WebhookSender webhookSender;

    @Override
    public void dispatch(WebhookPayload payload, UUID merchantId) {

        List<WebhookEndpoint> endpoints = repository.findAllByMerchant_IdAndStatus(merchantId, WebhookStatus.ACTIVE);

        for (WebhookEndpoint endpoint : endpoints) {

            long start = System.currentTimeMillis();
            WebhookDelivery delivery = null;

            try {

                String payloadJson = objectMapper.writeValueAsString(payload);

                delivery = deliveryService.createDelivery(endpoint, payload, payloadJson);

                ResponseEntity<?> response =
                        webhookSender.send(delivery);

                deliveryService.markDelivered(
                        delivery,
                        response,
                        System.currentTimeMillis() - start
                );

            } catch (Exception ex) {

                if (delivery != null) {
                    deliveryService.handleDeliveryFailure(delivery, ex, System.currentTimeMillis() - start);
                }

            }

        }
    }



}

