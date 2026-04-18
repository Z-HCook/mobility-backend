package com.wasel.backend.service;

import com.wasel.backend.dto.RegisterRequest;
import com.wasel.backend.exception.ValidationException;
import com.wasel.backend.exception.BusinessRuleException;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class InsertService {

    private final UserRepository userRepository;

    public InsertService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "users", key = "#request.email")
    public String register(RegisterRequest request) {

        // validation
        if (request.name == null || request.name.isBlank())
            throw new ValidationException("Name is required");

        if (request.email == null || request.email.isBlank())
            throw new ValidationException("Email is required");

        if (request.password == null || request.password.length() < 4)
            throw new ValidationException("Password must be at least 4 chars");

        // business rule
        if (userRepository.findByEmail(request.email).isPresent())
            throw new BusinessRuleException("Email already exists");

        User user = new User();
        user.setName(request.name);
        user.setEmail(request.email);
        user.setPassword(request.password);
        user.setRole(request.role);

        userRepository.save(user);

        return "User registered successfully";
    }
}