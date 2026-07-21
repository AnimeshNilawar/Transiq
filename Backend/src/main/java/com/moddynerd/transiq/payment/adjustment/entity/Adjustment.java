package com.moddynerd.transiq.payment.adjustment.entity;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "adjustments")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Adjustment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Merchant merchant;

    @Column(nullable = false, unique = true, length = 50)
    private String adjustmentReference;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AdjustmentType type;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false, length = 100)
    private String createdBy;
}
