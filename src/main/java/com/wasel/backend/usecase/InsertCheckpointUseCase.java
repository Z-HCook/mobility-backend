package com.wasel.backend.usecase;

import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.service.InsertCheckpointService;
import org.springframework.stereotype.Component;

@Component
public class InsertCheckpointUseCase {

    private final InsertCheckpointService insertCheckpointService;

    public InsertCheckpointUseCase(InsertCheckpointService insertCheckpointService) {
        this.insertCheckpointService = insertCheckpointService;
    }

    public String execute(InsertCheckpointRequest request) {
        return insertCheckpointService.insertCheckpoint(request);
    }
}