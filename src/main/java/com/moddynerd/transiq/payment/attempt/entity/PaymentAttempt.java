package com.moddynerd.transiq.payment.attempt.entity;

import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "payment_attempts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAttempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FailureCode failureCode;

    @Column(length = 500)
    private String failureMessage;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant completedAt;

    private Long processingTimeMs;
}