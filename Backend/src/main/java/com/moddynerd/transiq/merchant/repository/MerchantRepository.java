package com.moddynerd.transiq.merchant.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    Optional<Merchant> findByBusinessEmail(String businessEmail);

    boolean existsByBusinessEmail(String businessEmail);
}
