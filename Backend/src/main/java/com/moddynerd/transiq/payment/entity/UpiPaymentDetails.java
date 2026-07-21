package com.moddynerd.transiq.payment.entity;

import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "upi_payment_details")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpiPaymentDetails extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @Column(nullable = false, length = 100)
    private String upiId;

    @Column(length = 64)
    private String upiTransactionReference;

    @Column(length = 100)
    private String gatewayResponseCode;

    @Column(length = 255)
    private String gatewayMessage;
}
