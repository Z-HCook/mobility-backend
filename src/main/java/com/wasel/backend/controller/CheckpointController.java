package com.wasel.backend.controller;

import com.wasel.backend.dto.CheckpointHistoryResponse;
import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.service.CheckpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/checkpoints")
public class CheckpointController {
     private final CheckpointService checkpointService;

    public CheckpointController(CheckpointService checkpointService) {

        this.checkpointService = checkpointService;

    }

    @PostMapping
    public ResponseEntity<?> insertCheckpoint(@RequestBody InsertCheckpointRequest request) {
        var result = checkpointService.insertCheckpoint(request);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CheckpointHistory>> getCheckpointHistory(@PathVariable Integer id) {
        return ResponseEntity.ok(checkpointService.getByCheckpointId(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CheckpointHistory>> getCheckpointHistoryByDate(
            @PathVariable Integer id,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end
    ) {
        if (start != null && end != null) {
            return ResponseEntity.ok(
                    checkpointService.getByCheckpointIdAndDate(id, start, end)
            );
        }
        return ResponseEntity.ok(checkpointService.getByCheckpointId(id));
    }
}


