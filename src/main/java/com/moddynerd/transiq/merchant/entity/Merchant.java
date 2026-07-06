package com.moddynerd.transiq.merchant.entity;

import com.moddynerd.transiq.merchant.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "merchants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_merchants_business_email",
                        columnNames = "business_email"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(name = "business_email", nullable = false)
    private String businessEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MerchantStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;

        if (status == null){
            status = MerchantStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void onUpdate(){
        updatedAt = Instant.now();
    }

}

