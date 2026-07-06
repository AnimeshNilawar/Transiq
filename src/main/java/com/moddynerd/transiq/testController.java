package com.moddynerd.transiq;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {
    @GetMapping("/me")
    public Authentication me(Authentication authentication) {
        return authentication;
    }

}
