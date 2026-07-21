package com.moddynerd.transiq.admin.dto;

public record RevenueDataPoint(
        String date,
        long volume,
        long count
) {}
