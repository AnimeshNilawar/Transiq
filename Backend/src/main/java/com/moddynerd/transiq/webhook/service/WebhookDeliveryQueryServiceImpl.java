package com.moddynerd.transiq.webhook.service;

import com.moddynerd.transiq.webhook.dto.request.WebhookDeliveryFilter;
import com.moddynerd.transiq.webhook.dto.response.WebhookDeliveryResponse;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.exception.WebhookDeliveryNotFoundException;
import com.moddynerd.transiq.webhook.mapper.WebhookDeliveryMapper;
import com.moddynerd.transiq.webhook.repository.WebhookDeliveryRepository;
import com.moddynerd.transiq.webhook.specification.WebhookDeliverySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebhookDeliveryQueryServiceImpl implements WebhookDeliveryQueryService {

    private final WebhookDeliveryRepository repository;
    private final WebhookDeliveryMapper mapper;
    private final WebhookDeliverySpecification specification;

    @Override
    public Page<WebhookDeliveryResponse> getDeliveries(WebhookDeliveryFilter filter, Pageable pageable) {

        return repository.findAll(
                        specification.filter(filter),
                        pageable
                )
                .map(mapper::toResponse);
    }

    @Override
    public WebhookDeliveryResponse getDelivery(UUID id) {

        WebhookDelivery delivery = repository.findById(id).orElseThrow(() -> new WebhookDeliveryNotFoundException(id));

        return mapper.toResponse(delivery);
    }
}