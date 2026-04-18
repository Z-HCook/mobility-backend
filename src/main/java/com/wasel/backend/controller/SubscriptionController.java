package com.wasel.backend.controller;

import com.wasel.backend.dto.SubscriptionRequest;
import com.wasel.backend.model.Subscriptions;
import com.wasel.backend.service.SubscriptionService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد لجلب الـ IP
import org.springframework.http.HttpStatus;
import com.wasel.backend.usecase.SubscriptionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {



    private final SubscriptionUseCase subscriptionUseCase;
    private final SubscriptionService subscriptionService;
    private final RateLimitingService rateLimitingService; // 3. تعريف خدمة الحماية


    public SubscriptionController(SubscriptionService subscriptionService, RateLimitingService rateLimitingService ,SubscriptionUseCase subscriptionUseCase) {
        this.subscriptionService = subscriptionService;
        this.rateLimitingService = rateLimitingService;
        this.subscriptionUseCase = subscriptionUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionRequest subscriptionRequest, HttpServletRequest request) {
        // 5. فحص الـ Rate Limit
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            try {
                Subscriptions saved = subscriptionUseCase.create( subscriptionRequest);
                return ResponseEntity.status(200).body(saved);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Slow down! You are trying to subscribe too many times.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMySubscriptions(@PathVariable int userId, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many requests for subscription details.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> unsubscribe(@PathVariable int id, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            try {
                subscriptionUseCase.delete(id);
                return ResponseEntity.ok("Subscription deleted successfully");
            } catch (Exception e) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Wait a minute before trying to unsubscribe again.");
        }
    }
}