package com.moddynerd.transiq.payment.entity;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, unique = true, length = 50)
    private String paymentReference;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentMethodType paymentMethodType;

    @Builder.Default
    private Long refundedAmount = 0L;

    @Column(length = 255)
    private String customerEmail;

    @Column(length = 150)
    private String customerName;

    @Column(length = 100)
    private String orderId;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String clientSecretHash;

    @Column(unique = true, length = 100)
    private String idempotencyKey;

    @Column(nullable = false)
    private Instant expiresAt;

    @Lob
    private String metadata;

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    private CardPaymentDetails cardPaymentDetails;
}