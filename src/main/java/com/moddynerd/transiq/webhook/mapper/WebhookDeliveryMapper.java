package com.moddynerd.transiq.webhook.mapper;

import com.moddynerd.transiq.webhook.dto.response.WebhookDeliveryResponse;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import org.springframework.stereotype.Component;

@Component
public class WebhookDeliveryMapper {

    public WebhookDeliveryResponse toResponse(WebhookDelivery delivery) {

        return WebhookDeliveryResponse.builder()
                .id(delivery.getId())
                .eventId(delivery.getEvent().getEventId())
                .endpointId(delivery.getEndpoint().getId())
                .eventReference(delivery.getEvent().getReference())
                .eventType(delivery.getEvent().getEventType())
                .status(delivery.getStatus())
                .attemptCount(delivery.getAttemptCount())
                .httpStatus(delivery.getHttpStatus())
                .failureReason(delivery.getFailureReason())
                .durationMs(delivery.getDurationMs())
                .deliveredAt(delivery.getDeliveredAt())
                .createdAt(delivery.getCreatedAt())
                .lastAttemptAt(delivery.getLastAttemptAt())
                .nextRetryAt(delivery.getNextRetryAt())
                .build();
    }

}
