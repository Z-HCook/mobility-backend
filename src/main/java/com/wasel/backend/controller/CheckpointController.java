package com.wasel.backend.controller;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.service.CheckpointService;
import com.wasel.backend.service.RateLimitingService; // 1. استيراد الخدمة
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest; // 2. استيراد لجلب الـ IP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkpoints")
public class CheckpointController {

    private final CheckpointService checkpointService;
    private final RateLimitingService rateLimitingService; // 3. تعريف خدمة التحديد

    // 4. تحديث الـ Constructor
    public CheckpointController(CheckpointService checkpointService, RateLimitingService rateLimitingService) {
        this.checkpointService = checkpointService;
        this.rateLimitingService = rateLimitingService;
    }

    //@GetMapping("/{id}")
    //public Checkpoint getCheckpointById(@PathVariable int id) {
    //  return checkpointService.getCheckpointById(id);
    //}

    @PostMapping("/insert")
    public ResponseEntity<?> createCheckpoint(@RequestBody CheckpointRequest checkpointRequest, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(checkpointService.createCheckpoint(checkpointRequest));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("You have exceeded the limit for creating checkpoints. Please try again later.");
        }
    }

    // @DeleteMapping("/{id}")
    //public void deleteCheckpoint(@PathVariable int id) {
    // checkpointService.deleteCheckpoint(id);
    //}
}