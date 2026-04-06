package com.wasel.backend.service;


import com.wasel.backend.dto.RegisterRequest;
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

    // ✅ نمسح أي كاش متعلق بالمستخدمين عند تسجيل مستخدم جديد
    // استخدمنا البريد الإلكتروني كمفتاح لأنه وسيلة التحقق الأساسية.
    @CacheEvict(value = "users", key = "#request.email")
    public String register(RegisterRequest request) {


        if (userRepository.findByEmail(request.email).isPresent()) {
            return "Email already exists";
        }


        User user = new User();
        user.setName(request.name);
        user.setEmail(request.email);
        user.setPassword(request.password);
        user.setRole(request.role);


        userRepository.save(user);

        return "User registered successfully";
    }
}