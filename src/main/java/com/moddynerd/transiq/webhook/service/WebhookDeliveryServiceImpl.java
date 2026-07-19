package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEndpoint;
import com.moddynerd.transiq.webhook.mapper.WebhookEventTypeMapper;
import com.moddynerd.transiq.webhook.repository.WebhookDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class WebhookDeliveryServiceImpl implements WebhookDeliveryService{
    private final WebhookDeliveryRepository deliveryRepository;
    private final WebhookEventTypeMapper mapper;

    @Override
    public WebhookDelivery createDelivery(WebhookEndpoint endpoint, WebhookPayload payload) {

        WebhookDelivery delivery =
                WebhookDelivery.builder()
                        .endpoint(endpoint)
                        .eventId(payload.id())
                        .eventReference(payload.reference())
                        .eventType(mapper.toEventType(payload.type()))
                        .status(WebhookDeliveryStatus.PENDING)
                        .attemptCount(1)
                        .build();

        return deliveryRepository.save(delivery);
    }

    @Override
    public void markDelivered(WebhookDelivery delivery, ResponseEntity<?> response, long duration) {
        delivery.setStatus(WebhookDeliveryStatus.DELIVERED);
        delivery.setHttpStatus(response.getStatusCode().value());
        delivery.setDurationMs(duration);
        delivery.setDeliveredAt(Instant.now());

        deliveryRepository.save(delivery);
    }

    @Override
    public void markFailed(WebhookDelivery delivery, Exception exception, long duration) {
        delivery.setStatus(WebhookDeliveryStatus.FAILED);
        delivery.setDurationMs(duration);
        delivery.setDeliveredAt(Instant.now());
        delivery.setResponseBody(exception.getMessage());

        deliveryRepository.save(delivery);
    }


}
