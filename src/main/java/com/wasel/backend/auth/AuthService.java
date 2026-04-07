package com.wasel.backend.auth;

import com.wasel.backend.model.User;
import com.wasel.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo,
                       JwtService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepo.findByEmail(request.email)
                .orElse(null);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(request.password)) {
            return null;
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getRole()
        );

        return new LoginResponse(token);
    }
}