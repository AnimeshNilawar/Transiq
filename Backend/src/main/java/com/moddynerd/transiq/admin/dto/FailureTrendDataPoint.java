package com.moddynerd.transiq.admin.dto;

public record FailureTrendDataPoint(
        String date,
        long succeeded,
        long failed
) {}
