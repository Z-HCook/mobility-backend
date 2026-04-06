package com.wasel.backend.controller;

import com.wasel.backend.dto.RegisterRequest;
import com.wasel.backend.service.InsertService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد لجلب الـ IP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insert")
public class InsertController {

    private final InsertService insservice;
    private final RateLimitingService rateLimitingService;

    public InsertController(InsertService insertservice, RateLimitingService rateLimitingService) {
        this.insservice = insertservice;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            String result = insservice.register(registerRequest);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many registration attempts. Please try again after a minute.");
        }
    }
}