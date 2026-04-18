package com.wasel.backend.controller;

import com.wasel.backend.dto.SubscriptionRequest;
import com.wasel.backend.model.Subscriptions;
import com.wasel.backend.usecase.SubscriptionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionUseCase subscriptionUseCase;

    public SubscriptionController(SubscriptionUseCase subscriptionUseCase) {
        this.subscriptionUseCase = subscriptionUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<Subscriptions> subscribe(@RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionUseCase.create(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscriptions>> getMySubscriptions(@PathVariable int userId) {
        return ResponseEntity.ok(subscriptionUseCase.getByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> unsubscribe(@PathVariable int id) {
        subscriptionUseCase.delete(id);
        return ResponseEntity.ok("تم حذف الاشتراك بنجاح ✅");
    }
}