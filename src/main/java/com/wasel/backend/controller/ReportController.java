package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.service.ReportService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد لجلب الـ IP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService service;
    private final RateLimitingService rateLimitingService;

    public ReportController(ReportService service, RateLimitingService rateLimitingService) {
        this.service = service;
        this.rateLimitingService = rateLimitingService;
    }


    @PostMapping
    public ResponseEntity<String> insertReport(@RequestBody InsertReportRequest reportRequest, HttpServletRequest request) {

        String key = request.getRemoteAddr() + ":" + reportRequest.userId;
        Bucket bucket = rateLimitingService.resolveBucket(key);

        if (bucket.tryConsume(1)) {
            var result = service.insertReport(reportRequest);
            return ResponseEntity.status(200).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("You've sent too many reports. Please wait a minute before submitting a new one.");
        }
    }
}