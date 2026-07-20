package com.moddynerd.transiq.dashboard.dto;

import com.moddynerd.transiq.payment.ledger.entity.EntrySide;
import com.moddynerd.transiq.payment.ledger.entity.LedgerAccount;
import com.moddynerd.transiq.payment.ledger.entity.LedgerEntryType;

import java.time.Instant;
import java.util.UUID;

public record DashboardLedgerEntryResponse(

        UUID id,

        LedgerEntryType entryType,

        LedgerAccount account,

        EntrySide side,

        Long amount,

        String description,

        String relatedReference,

        Instant createdAt

) {}
