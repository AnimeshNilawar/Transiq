package com.moddynerd.transiq.auth.controller;

import com.moddynerd.transiq.auth.dto.LoginRequest;
import com.moddynerd.transiq.auth.dto.LoginResponse;
import com.moddynerd.transiq.auth.dto.RegisterRequest;
import com.moddynerd.transiq.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ){
        return authService.login(request);
    }
}
