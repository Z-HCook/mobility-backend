package com.wasel.backend.service;

import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.repository.CheckpointHistoryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckpointHistoryService {

    private final CheckpointHistoryRepository checkpointHistoryRepository;

    public CheckpointHistoryService(CheckpointHistoryRepository checkpointHistoryRepository) {
        this.checkpointHistoryRepository = checkpointHistoryRepository;
    }

    @Cacheable(value = "checkpointHistory", key = "#checkpointId")
    public List<CheckpointHistory> getByCheckpointId(Integer checkpointId) {
        return checkpointHistoryRepository.findByCheckpoint_Id(checkpointId);
    }

    @Cacheable(value = "checkpointHistoryRange", key = "#checkpointId.toString() + #start.toString() + #end.toString()")
    public List<CheckpointHistory> getByCheckpointIdAndDate(
            Integer checkpointId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return checkpointHistoryRepository.findByCheckpoint_IdAndInsAtBetween(
                checkpointId, start, end);
    }
}