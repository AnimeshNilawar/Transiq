package com.moddynerd.transiq.dashboard.specification;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentSpecification {

    public Specification<Payment> filter(
            Merchant merchant,
            PaymentStatus status,
            Instant from,
            Instant to,
            String orderId
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("merchant"), merchant)
            );

            if (status != null) {
                predicates.add(
                        cb.equal(root.get("status"), status)
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

            if (orderId != null && !orderId.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("orderId")),
                                "%" + orderId.toLowerCase() + "%"
                        )
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
