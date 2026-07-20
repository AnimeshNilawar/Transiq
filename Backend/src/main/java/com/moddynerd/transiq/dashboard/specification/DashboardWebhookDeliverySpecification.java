package com.moddynerd.transiq.dashboard.specification;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.webhook.entity.WebhookDelivery;
import com.moddynerd.transiq.webhook.entity.WebhookDeliveryStatus;
import com.moddynerd.transiq.webhook.entity.WebhookEventType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DashboardWebhookDeliverySpecification {

    public Specification<WebhookDelivery> filter(
            Merchant merchant,
            WebhookDeliveryStatus status,
            WebhookEventType eventType,
            UUID endpointId,
            Instant from,
            Instant to
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(
                            root.get("endpoint").get("merchant"),
                            merchant
                    )
            );

            if (status != null) {
                predicates.add(
                        cb.equal(root.get("status"), status)
                );
            }

            if (eventType != null) {
                predicates.add(
                        cb.equal(
                                root.get("event").get("eventType"),
                                eventType
                        )
                );
            }

            if (endpointId != null) {
                predicates.add(
                        cb.equal(
                                root.get("endpoint").get("id"),
                                endpointId
                        )
                );
            }

            if (from != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                from
                        )
                );
            }

            if (to != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                to
                        )
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
