package com.moddynerd.transiq.payment.settlement.entity;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "settlements")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Settlement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Merchant merchant;

    @Column(nullable = false, unique = true, length = 40)
    private String settlementReference;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

    private Instant processedAt;

    @Column(length = 100)
    private String bankReference;
}