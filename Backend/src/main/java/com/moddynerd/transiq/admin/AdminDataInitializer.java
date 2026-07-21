package com.moddynerd.transiq.admin;

import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.auth.enums.UserRole;
import com.moddynerd.transiq.auth.repository.MerchantUserRepository;
import com.moddynerd.transiq.merchant.repository.MerchantRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1000)
@RequiredArgsConstructor
@Slf4j
public class AdminDataInitializer implements CommandLineRunner {

    private final MerchantRepository merchantRepository;
    private final MerchantUserRepository merchantUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        entityManager.createNativeQuery(
                "ALTER TABLE merchant_users DROP CONSTRAINT IF EXISTS merchant_users_role_check"
        ).executeUpdate();

        var platform = merchantRepository.findByBusinessEmail("platform@transiq.com")
                .orElseGet(() -> merchantRepository.save(
                        com.moddynerd.transiq.merchant.entity.Merchant.builder()
                                .businessName("Transiq Platform")
                                .businessEmail("platform@transiq.com")
                                .build()
                ));

        if (merchantUserRepository.findByEmail("admin@transiq.com").isEmpty()) {
            MerchantUser admin = MerchantUser.builder()
                    .firstName("Platform")
                    .lastName("Admin")
                    .email("admin@transiq.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.PLATFORM_ADMIN)
                    .merchant(platform)
                    .build();
            merchantUserRepository.save(admin);
            log.info("Created default platform admin: admin@transiq.com / admin123");
        }
    }
}
