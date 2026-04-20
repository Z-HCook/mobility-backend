package com.wasel.backend.controller;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.service.CheckpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/v1/checkpoints")
public class CheckpointController {

    private final CheckpointService checkpointService;

    public CheckpointController(CheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }


    //@GetMapping
    //public List<Checkpoint> getCheckpointHistory() {
      //  return checkpointService.getCheckpointHistory();

    //}

    //@GetMapping("/{id}")
    //public Checkpoint getCheckpointById(@PathVariable int id) {
      //  return checkpointService.getCheckpointById(id);
    //}

    @PostMapping("/insert")
    public ResponseEntity<?> createCheckpoint(@RequestBody CheckpointRequest request) {
        return ResponseEntity.ok(checkpointService.createCheckpoint(request));
    }

   // @DeleteMapping("/{id}")
    //public void deleteCheckpoint(@PathVariable int id) {
       // checkpointService.deleteCheckpoint(id);
    //}
}