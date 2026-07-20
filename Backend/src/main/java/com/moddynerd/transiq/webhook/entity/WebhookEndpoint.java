package com.moddynerd.transiq.webhook.entity;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "webhook_endpoints")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEndpoint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Merchant merchant;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private String encryptedSecret;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatus status;

}