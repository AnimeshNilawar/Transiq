package com.moddynerd.transiq.apikey.entity;

import com.moddynerd.transiq.merchant.entity.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "api_keys",
        indexes = {
                @Index(name = "idx_api_key_prefix", columnList = "keyPrefix"),
                @Index(name = "idx_api_key_merchant", columnList = "merchant_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String keyPrefix;

    @Column(nullable = false)
    private String keyHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiKeyType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiKeyEnvironment environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiKeyStatus status;

    private Instant lastUsedAt;

    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
