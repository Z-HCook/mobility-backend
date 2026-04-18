package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.service.InsertCheckpointService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد عشان الـ IP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insert")
public class InsertCheckpointController {

    private final InsertCheckpointService checkpointService;
    private final RateLimitingService rateLimitingService;

    public InsertCheckpointController(InsertCheckpointService checkpointService, RateLimitingService rateLimitingService) {
        this.checkpointService = checkpointService;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/checkpoint")
    public ResponseEntity<String> insertCheckpoint(@RequestBody InsertCheckpointRequest insertRequest, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            String result = checkpointService.insertCheckpoint(insertRequest);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("You're moving too fast! Please wait a moment before inserting more data.");
        }
    }
}