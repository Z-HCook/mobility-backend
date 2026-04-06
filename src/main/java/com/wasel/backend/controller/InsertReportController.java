package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.service.InsertReportService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد لجلب الـ IP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insert")
public class InsertReportController {

    private final InsertReportService service;
    private final RateLimitingService rateLimitingService;

    public InsertReportController(InsertReportService service, RateLimitingService rateLimitingService) {
        this.service = service;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/report")
    public ResponseEntity<String> insertReport(@RequestBody InsertReportRequest reportRequest, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            String result = service.insertReport(reportRequest);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("You've sent too many reports. Please wait a minute before submitting a new one.");
        }
    }
}