package com.wasel.backend.controller;

import com.wasel.backend.model.User;
import com.wasel.backend.service.UserService;
import com.wasel.backend.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.wasel.backend.usecase.UserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserUseCase userUseCase;
    private final RateLimitingService rateLimitingService;

    public UserController(UserService service , UserUseCase userUseCase ,  RateLimitingService rateLimitingService) {
        this.service = service;
        this.rateLimitingService = rateLimitingService;
        this.userUseCase = userUseCase;
    }


    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());
        if (bucket.tryConsume(1)) {
            User created = userUseCase.create(user);
            return ResponseEntity.status(201).body(created);

        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("User creation limit reached. Try again later.");
        }
    }


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


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());
        User user = userUseCase.getById(id);
        if (bucket.tryConsume(1)) {
            if (user == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(user);
            }
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Request limit exceeded.");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());
        if (bucket.tryConsume(1)) {
            userUseCase.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Slow down! Too many delete attempts.");
        }
    }
}