package com.moddynerd.transiq.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePaymentStatusRequest(
        @NotBlank String status
) {}
