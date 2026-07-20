package com.moddynerd.transiq.merchant.dto;

import com.moddynerd.transiq.merchant.enums.MerchantStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MerchantResponse {
    private UUID id;
    private String businessName;
    private String businessEmail;
    private MerchantStatus status;
    private Instant createdAt;
}
