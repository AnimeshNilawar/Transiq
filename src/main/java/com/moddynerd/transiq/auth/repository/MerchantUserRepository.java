package com.moddynerd.transiq.auth.repository;

import com.moddynerd.transiq.auth.entity.MerchantUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantUserRepository extends JpaRepository<MerchantUser, UUID> {
    Optional<MerchantUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
