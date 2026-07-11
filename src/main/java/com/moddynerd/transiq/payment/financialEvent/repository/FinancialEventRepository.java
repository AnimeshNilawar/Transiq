package com.moddynerd.transiq.payment.financialEvent.repository;

import com.moddynerd.transiq.payment.financialEvent.entity.FinancialEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FinancialEventRepository
        extends JpaRepository<FinancialEvent, UUID> {
}