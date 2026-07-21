package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.auth.exception.ResourceNotFoundException;
import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import com.moddynerd.transiq.webhook.entity.WebhookEvent;
import com.moddynerd.transiq.webhook.mapper.WebhookEventTypeMapper;
import com.moddynerd.transiq.webhook.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WebhookEventServiceImpl implements WebhookEventService {

    private final WebhookEventRepository eventRepository;
    private final WebhookEventTypeMapper eventTypeMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public WebhookEvent createEvent(
            WebhookPayload payload,
            UUID merchantId
    ) {

        String payloadJson;

        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to serialize webhook payload", ex
            );
        }

        WebhookEvent event = WebhookEvent.builder()
                .eventId(payload.id())
                .merchantId(merchantId)
                .reference(payload.reference())
                .eventType(eventTypeMapper.toEventType(payload.type()))
                .payload(payloadJson)
                .build();

        return eventRepository.save(event);
    }

    @Override
    public WebhookEvent findByEventId(UUID eventId) {
        return eventRepository.findByEventId(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Webhook event not found: " + eventId
                        ));
    }

    @Override
    public WebhookEvent findById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Webhook event not found: " + id
                        ));
    }

}
