package com.moddynerd.transiq.webhook.dispatcher;

import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.entity.WebhookEvent;
import com.moddynerd.transiq.webhook.entity.WebhookStatus;
import com.moddynerd.transiq.webhook.repository.WebhookEndpointRepository;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryExecutor;
import com.moddynerd.transiq.webhook.service.WebhookDeliveryService;
import com.moddynerd.transiq.webhook.service.WebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpWebhookDispatcher implements WebhookDispatcher {

    private final WebhookEndpointRepository endpointRepository;
    private final WebhookEventService eventService;
    private final WebhookDeliveryService deliveryService;
    private final WebhookDeliveryExecutor executor;

    @Override
    public void dispatch(
            WebhookPayload payload,
            UUID merchantId
    ) {

        WebhookEvent event =
                eventService.createEvent(payload, merchantId);

        List<WebhookEndpoint> endpoints =
                endpointRepository.findAllByMerchant_IdAndStatus(
                        merchantId,
                        WebhookStatus.ACTIVE
                );

        for (WebhookEndpoint endpoint : endpoints) {

            try {

                WebhookDelivery delivery =
                        deliveryService.createDelivery(endpoint, event);

                executor.execute(delivery);

            } catch (Exception ex) {

                log.error(
                        "Failed to create webhook delivery for endpoint {}",
                        endpoint.getId(),
                        ex
                );

            }

        }
    }

}
