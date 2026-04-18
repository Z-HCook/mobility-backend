package com.wasel.backend.controller;

import com.wasel.backend.dto.SubscriptionRequest;
import com.wasel.backend.model.Subscriptions;
import com.wasel.backend.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }


    public ResponseEntity<?> subscribe(@RequestBody SubscriptionRequest request) {
        try {
            Subscriptions saved = subscriptionService.createSubscription(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    public ResponseEntity<List<Subscriptions>> getMySubscriptions(@PathVariable int userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> unsubscribe(@PathVariable int id) {
        try {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.ok("تم حذف الاشتراك بنجاح ✅");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}