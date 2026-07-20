package com.moddynerd.transiq.payment.ledger.entity;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import com.moddynerd.transiq.payment.settlement.entity.Settlement;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;



@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private FinancialEvent financialEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerEntryType entryType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerAccount account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntrySide side;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 300)
    private String description;
}