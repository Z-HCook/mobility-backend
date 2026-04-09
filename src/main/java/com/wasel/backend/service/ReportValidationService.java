package com.wasel.backend.service;


import com.wasel.backend.model.User;
import com.wasel.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportValidationService {

    private final UserRepository userRepository;

    public ReportValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User validateUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}