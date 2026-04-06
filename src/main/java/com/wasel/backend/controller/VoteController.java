package com.wasel.backend.controller;

import com.wasel.backend.dto.VoteRequest;
import com.wasel.backend.service.VoteService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد لجلب الـ IP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService service;
    private final RateLimitingService rateLimitingService;

    public VoteController(VoteService service, RateLimitingService rateLimitingService) {
        this.service = service;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping
    public ResponseEntity<String> vote(@RequestBody VoteRequest voteRequest, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            String result = service.vote(voteRequest);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("You're voting too fast! Please wait a moment.");
        }
    }
}