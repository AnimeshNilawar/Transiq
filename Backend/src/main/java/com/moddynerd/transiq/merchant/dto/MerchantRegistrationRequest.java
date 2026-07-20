package com.moddynerd.transiq.merchant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantRegistrationRequest {

    @NotBlank(message = "Business Name is required")
    private String businessName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Business email is required")
    private String businessEmail;
}
