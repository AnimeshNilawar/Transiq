package com.moddynerd.transiq.auth.repository;

import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantUserRepository extends JpaRepository<MerchantUser, UUID> {
    Optional<MerchantUser> findByEmail(String email);

    @Query("SELECT mu FROM MerchantUser mu JOIN FETCH mu.merchant WHERE mu.email = :email")
    Optional<MerchantUser> findByEmailWithMerchant(@Param("email") String email);

    boolean existsByEmail(String email);

    List<MerchantUser> findByMerchantOrderByCreatedAtAsc(Merchant merchant);
}
