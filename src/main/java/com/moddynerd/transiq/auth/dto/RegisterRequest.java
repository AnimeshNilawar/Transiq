package com.moddynerd.transiq.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    String businessName;
    String businessEmail;

    String firstName;
    String lastName;

    String email;
    String password;
}
