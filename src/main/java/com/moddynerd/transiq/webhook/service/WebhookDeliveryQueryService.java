package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.dto.request.WebhookDeliveryFilter;
import com.moddynerd.transiq.webhook.dto.response.WebhookDeliveryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WebhookDeliveryQueryService {

    Page<WebhookDeliveryResponse> getDeliveries(
            WebhookDeliveryFilter filter,
            Pageable pageable
    );

    WebhookDeliveryResponse getDelivery(
            UUID id
    );

}