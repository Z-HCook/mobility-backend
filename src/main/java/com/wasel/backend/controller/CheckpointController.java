
package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.usecase.InsertCheckpointUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkpoints")
public class CheckpointController {

    private final InsertCheckpointUseCase insertCheckpointUseCase;

    public CheckpointController(InsertCheckpointUseCase insertCheckpointUseCase) {
        this.insertCheckpointUseCase = insertCheckpointUseCase;
    }

    @PostMapping("/insert")
    public ResponseEntity<?> createCheckpoint(@RequestBody InsertCheckpointRequest request) {
        return ResponseEntity.ok(insertCheckpointUseCase.execute(request));
    }
}