package com.moddynerd.transiq.admin.dto;

import java.util.List;

public record AdminDashboardResponse(
        long totalMerchants,
        long totalPayments,
        long totalVolume,
        long totalRefunds,
        long activeApiKeys,
        List<RecentPayment> recentPayments
) {
    public record RecentPayment(
            String paymentReference,
            Long amount,
            String currency,
            String status,
            String merchantName,
            String createdAt
    ) {}
}
