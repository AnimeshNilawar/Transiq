package com.moddynerd.transiq.payment.financialEvent.entity;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "financial_events")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinancialEventType type;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Column(length = 500)
    private String description;
}