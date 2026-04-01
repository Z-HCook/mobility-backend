package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.service.InsertCheckpointService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insert")
public class InsertCheckpointController {

    private final InsertCheckpointService checkpointService;

    public InsertCheckpointController(InsertCheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }

    @PostMapping("/checkpoint")
    public String insertCheckpoint(@RequestBody InsertCheckpointRequest request) {
        return checkpointService.insertCheckpoint(request);
    }
}