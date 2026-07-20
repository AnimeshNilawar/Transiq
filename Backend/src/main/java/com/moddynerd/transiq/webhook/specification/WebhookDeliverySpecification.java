package com.moddynerd.transiq.webhook.specification;

import com.moddynerd.transiq.webhook.dto.request.WebhookDeliveryFilter;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebhookDeliverySpecification {

    public Specification<WebhookDelivery> filter(
            WebhookDeliveryFilter filter
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.status() != null) {
                predicates.add(
                        cb.equal(
                                root.get("status"),
                                filter.status()
                        )
                );
            }

            if (filter.eventType() != null) {
                predicates.add(
                        cb.equal(
                                root.get("event").get("eventType"),
                                filter.eventType()
                        )
                );
            }

            if (filter.endpointId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("endpoint").get("id"),
                                filter.endpointId()
                        )
                );
            }

            if (filter.eventId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("event").get("eventId"),
                                filter.eventId()
                        )
                );
            }

            if (filter.from() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                filter.from()
                        )
                );
            }

            if (filter.to() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                filter.to()
                        )
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));

        };

    }

}
