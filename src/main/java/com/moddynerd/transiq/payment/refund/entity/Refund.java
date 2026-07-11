package com.moddynerd.transiq.payment.refund.entity;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Refund extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Merchant merchant;

    @Column(nullable = false)
    private String idempotencyKey;

    @Column(nullable = false, unique = true)
    private String refundReference;

    @Column(nullable = false, precision = 19, scale = 2)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundReason reason;
}