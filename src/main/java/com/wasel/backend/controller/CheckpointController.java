package com.wasel.backend.controller;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.service.CheckpointService;
import com.wasel.backend.service.RateLimitingService;
import com.wasel.backend.usecase.InsertCheckpointUseCase;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/checkpoints")
public class CheckpointController {

    private final CheckpointService checkpointService;
   private final RateLimitingService rateLimitingService;
   private  final InsertCheckpointUseCase insertCheckpointUseCase;

    public CheckpointController(CheckpointService checkpointService , RateLimitingService rateLimitingService , InsertCheckpointUseCase insertCheckpointUseCase) {
        this.insertCheckpointUseCase = insertCheckpointUseCase;
        this.checkpointService = checkpointService;
        this.rateLimitingService = rateLimitingService;
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getCheckpointHistory(@PathVariable Integer id, HttpServletRequest request) {
        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(checkpointService.getByCheckpointId(id));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Slow down! You've reached the limit of requests per minute.");
        }
    }


    public ResponseEntity<?> getCheckpointHistorybydate(@PathVariable Integer id, @RequestParam(required = false) LocalDateTime start,
                                                  @RequestParam(required = false) LocalDateTime end, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            if (start != null && end != null) {
                return ResponseEntity.ok(
                        checkpointService.getByCheckpointIdAndDate(id, start, end)
                );
            }

            return ResponseEntity.ok(
                    checkpointService.getByCheckpointId(id)
            );
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Slow down! You've reached the limit of requests per minute.");
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> createCheckpoint(@RequestBody InsertCheckpointRequest checkpointRequest, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            return ResponseEntity.status(201)
                    .body(insertCheckpointUseCase.execute(checkpointRequest));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("You have exceeded the limit for creating checkpoints. Please try again later.");
        }

    }
}