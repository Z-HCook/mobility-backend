package com.wasel.backend.controller;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.service.CheckpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkpoints")
public class CheckpointController {

    private final CheckpointService checkpointService;

    public CheckpointController(CheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }


    //@GetMapping
    //public List<Checkpoint> getCheckpointHistory() {
      //  return checkpointService.getCheckpointHistory();

    //}

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CheckpointHistory>> getCheckpointHistory(
            @PathVariable Integer id,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end
    ) {

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
        if (start != null && end != null) {
            return ResponseEntity.ok(
                    checkpointService.getByCheckpointIdAndDate(id, start, end)
            );
        }

        return ResponseEntity.ok(
                checkpointService.getByCheckpointId(id)
        );
    }
}