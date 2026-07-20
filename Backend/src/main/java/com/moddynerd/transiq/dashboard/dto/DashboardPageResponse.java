package com.moddynerd.transiq.dashboard.dto;

import java.util.List;

public record DashboardPageResponse<T>(

        List<T> content,

        int page,

        int size,

        long totalElements,

        int totalPages

) {}
