package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.usecase.InsertCheckpointUseCase;
import com.wasel.backend.service.CheckpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/checkpoints")
public class CheckpointController {

    private final InsertCheckpointUseCase insertCheckpointUseCase;
    private final CheckpointService checkpointService;

    public CheckpointController(
            InsertCheckpointUseCase insertCheckpointUseCase,
            CheckpointService checkpointService
    ) {
        this.insertCheckpointUseCase = insertCheckpointUseCase;
        this.checkpointService = checkpointService;
    }

    @PostMapping
    public ResponseEntity<?> createCheckpoint(@RequestBody InsertCheckpointRequest request) {
        return ResponseEntity.status(201)
                .body(insertCheckpointUseCase.execute(request));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CheckpointHistory>> getCheckpointHistory(
            @PathVariable Integer id,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end
    ) {

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