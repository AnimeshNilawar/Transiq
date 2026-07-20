package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.entity.WebhookEvent;
import com.moddynerd.transiq.webhook.entity.WebhookStatus;
import com.moddynerd.transiq.webhook.repository.WebhookEndpointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WebhookReplayServiceImpl implements WebhookReplayService {

    private final WebhookEventService eventService;
    private final WebhookEndpointRepository endpointRepository;
    private final WebhookDeliveryService deliveryService;
    private final WebhookDeliveryExecutor executor;

    @Override
    public void replayEvent(UUID eventId) {

        WebhookEvent event = eventService.findByEventId(eventId);

        log.info("Replaying webhook event {}", eventId);

        List<WebhookEndpoint> endpoints =
                endpointRepository.findAllByMerchant_IdAndStatus(
                        event.getMerchantId(),
                        WebhookStatus.ACTIVE
                );

        log.info("Found {} active endpoints", endpoints.size());

        for (WebhookEndpoint endpoint : endpoints) {

            WebhookDelivery delivery =
                    deliveryService.createDelivery(endpoint, event);

            log.info(
                    "Created replay delivery {} for endpoint {}",
                    delivery.getId(),
                    endpoint.getId()
            );

            executor.execute(delivery);

        }

        log.info("Replay completed for event {}", eventId);

    }

}
