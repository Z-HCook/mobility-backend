package com.wasel.backend.controller;

import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.service.CheckpointHistoryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/checkpoint-history")
public class CheckpointHistoryController {

    private final CheckpointHistoryService checkpointHistoryService;

    public CheckpointHistoryController(CheckpointHistoryService checkpointHistoryService) {
        this.checkpointHistoryService = checkpointHistoryService;
    }

    // 📌 Get history by checkpoint id
    @GetMapping("/{checkpointId}")
    public List<CheckpointHistory> getByCheckpoint(@PathVariable Integer checkpointId) {
        return checkpointHistoryService.getByCheckpointId(checkpointId);
    }

    // 📌 Get history by checkpoint + date range
    @GetMapping("/{checkpointId}/filter")
    public List<CheckpointHistory> getByDateRange(
            @PathVariable Integer checkpointId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return checkpointHistoryService
                .getByCheckpointIdAndDate(checkpointId, start, end);
    }
}