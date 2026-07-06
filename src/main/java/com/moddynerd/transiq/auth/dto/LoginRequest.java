package com.moddynerd.transiq.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    String email;
    String password;
}
