package com.wasel.backend.usecase;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.service.CheckpointService;
import org.springframework.stereotype.Component;

@Component
public class InsertCheckpointUseCase {

    private final CheckpointService CheckpointService;

    public InsertCheckpointUseCase(CheckpointService CheckpointService) {
        this.CheckpointService = CheckpointService;
    }

    public String execute(InsertCheckpointRequest request) {
        return CheckpointService.insertCheckpoint(request);
    }
}