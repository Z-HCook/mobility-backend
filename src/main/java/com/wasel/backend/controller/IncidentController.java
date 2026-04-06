package com.wasel.backend.controller;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.service.IncidentVerificationService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد عشان الـ IP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentVerificationService service;
    private final RateLimitingService rateLimitingService;

    public IncidentController(IncidentVerificationService service, RateLimitingService rateLimitingService) {
        this.service = service;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyReportRequest verifyRequest, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            String result = service.verifyReport(verifyRequest);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many verification attempts. Please wait a minute before trying again.");
        }
    }
}