package com.wasel.backend.controller;

import com.wasel.backend.model.User;
import com.wasel.backend.service.UserService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد لجلب الـ IP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final RateLimitingService rateLimitingService;

    public UserController(UserService service, RateLimitingService rateLimitingService) {
        this.service = service;
        this.rateLimitingService = rateLimitingService;
    }

    // ✅ Create
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(service.createUser(user));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("User creation limit reached. Try again later.");
        }
    }

    // ✅ Get All
    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(service.getAllUsers());
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many requests for user list.");
        }
    }

    // ✅ Get By Id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(service.getUserById(id));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Request limit exceeded.");
        }
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());
        if (bucket.tryConsume(1)) {
            service.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Slow down! Too many delete attempts.");
        }
    }
}