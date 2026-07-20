package com.moddynerd.transiq.dashboard.specification;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntry;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class LedgerEntrySpecification {

    public Specification<LedgerEntry> filter(
            Merchant merchant,
            LedgerAccount account,
            Instant from,
            Instant to
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("merchant"), merchant)
            );

            if (account != null) {
                predicates.add(
                        cb.equal(root.get("account"), account)
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
