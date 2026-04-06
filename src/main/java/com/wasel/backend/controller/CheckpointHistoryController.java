package com.wasel.backend.controller;

import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.service.CheckpointHistoryService;
import com.wasel.backend.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ch")
public class CheckpointHistoryController {

    private final CheckpointHistoryService service;
    private final RateLimitingService rateLimitingService;

    public CheckpointHistoryController(CheckpointHistoryService service, RateLimitingService rateLimitingService) {
        this.service = service;
        this.rateLimitingService = rateLimitingService;
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getCheckpointHistory(@PathVariable Integer id, HttpServletRequest request) {
        // فحص الـ Rate Limit
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(service.getByCheckpointId(id));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Slow down! You've reached the limit of requests per minute.");
        }
    }

    @GetMapping("/{id}/history/filter")
    public ResponseEntity<?> getCheckpointHistoryByDate(
            @PathVariable Integer id,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            HttpServletRequest request
    ) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(service.getByCheckpointIdAndDate(id, start, end));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many filter requests. Please wait a moment.");
        }
    }
}