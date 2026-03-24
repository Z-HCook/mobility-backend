package com.wasel.backend.controller;

import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.service.CheckpointService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkpoints")
public class CheckpointController {

    private final CheckpointService checkpointService;

    public CheckpointController(CheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }

    @GetMapping
    public List<Checkpoint> getAllCheckpoints() {
        return checkpointService.getAllCheckpoints();
    }

    @GetMapping("/{id}")
    public Checkpoint getCheckpointById(@PathVariable int id) {
        return checkpointService.getCheckpointById(id);
    }

    @PostMapping
    public Checkpoint createCheckpoint(@RequestBody Checkpoint checkpoint) {
        return checkpointService.createCheckpoint(checkpoint);
    }

    @DeleteMapping("/{id}")
    public void deleteCheckpoint(@PathVariable int id) {
        checkpointService.deleteCheckpoint(id);
    }
}