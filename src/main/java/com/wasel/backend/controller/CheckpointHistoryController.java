package com.wasel.backend.controller;

import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.service.CheckpointHistoryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ch")
public class CheckpointHistoryController {

    private final CheckpointHistoryService service;

    public CheckpointHistoryController(CheckpointHistoryService service) {
        this.service = service;
    }

    @GetMapping("/{id}/history")
    public List<CheckpointHistory> getCheckpointHistory(@PathVariable Integer id) {
        return service.getByCheckpointId(id);
    }

    @GetMapping("/{id}/history/filter")
    public List<CheckpointHistory> getCheckpointHistoryByDate(
            @PathVariable Integer id,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return service.getByCheckpointIdAndDate(id, start, end);
    }
}