package com.moddynerd.transiq.dashboard.dto;

import com.moddynerd.transiq.auth.enums.UserRole;
import com.moddynerd.transiq.merchant.enums.MerchantStatus;

import java.util.UUID;

public record DashboardMeResponse(

        UUID userId,

        String email,

        String firstName,

        String lastName,

        UserRole role,

        DashboardMerchantInfo merchant

) {

    public record DashboardMerchantInfo(

            UUID id,

            String businessName,

            String businessEmail,

            MerchantStatus status

    ) {}
}
