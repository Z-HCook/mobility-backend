package com.wasel.backend.auth;

import com.wasel.backend.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest request) {

        LoginResponse response = service.login(request);

        if (response == null) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return response;
    }
}