package com.moddynerd.transiq.dashboard.specification;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.refund.entity.Refund;
import com.moddynerd.transiq.payment.refund.entity.RefundStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class RefundSpecification {

    public Specification<Refund> filter(
            Merchant merchant,
            RefundStatus status,
            Instant from,
            Instant to,
            String paymentReference
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

            if (paymentReference != null && !paymentReference.isBlank()) {
                predicates.add(
                        cb.equal(
                                root.get("payment").get("paymentReference"),
                                paymentReference
                        )
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
