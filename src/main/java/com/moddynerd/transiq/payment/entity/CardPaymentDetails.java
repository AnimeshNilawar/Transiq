package com.moddynerd.transiq.payment.entity;

import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "card_payment_details")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentDetails extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardNetwork cardNetwork;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BankCode issuerBank;

    @Column(length = 19)
    private String maskedCardNumber;

    @Column(nullable = false)
    private Integer expiryMonth;

    @Column(nullable = false)
    private Integer expiryYear;

    @Column(length = 64)
    private String authorizationCode;

    @Column(length = 100)
    private String gatewayResponseCode;

    @Column(length = 255)
    private String gatewayMessage;

}
