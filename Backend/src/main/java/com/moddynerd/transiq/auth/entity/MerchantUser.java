package com.moddynerd.transiq.auth.entity;

import com.moddynerd.transiq.auth.enums.UserRole;
import com.moddynerd.transiq.merchant.entity.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "merchant_users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_merchant_users_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    @Builder.Default
    private boolean mustChangePassword = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "merchant_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_merchant")
    )
    private Merchant merchant;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate(){
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;

        if (role == null){
            role = UserRole.OWNER;
        }
        enabled = true;
    }

    @PreUpdate
    public void onUpdate(){
        updatedAt = Instant.now();
    }
}
