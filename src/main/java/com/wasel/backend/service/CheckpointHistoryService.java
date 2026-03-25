package com.wasel.backend.service;

import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.repository.CheckpointHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckpointHistoryService {

    private final CheckpointHistoryRepository checkpointHistoryRepository;

    public CheckpointHistoryService(CheckpointHistoryRepository checkpointHistoryRepository) {
        this.checkpointHistoryRepository = checkpointHistoryRepository;
    }

    // 🔹 كل الهيستوري لشيكبوينت معين
    public List<CheckpointHistory> getByCheckpointId(Integer checkpointId) {
        return checkpointHistoryRepository.findByCheckpointId(checkpointId);
    }

    // 🔹 الهيستوري ضمن فترة زمنية
    public List<CheckpointHistory> getByCheckpointIdAndDate(
            Integer checkpointId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return checkpointHistoryRepository.findByCheckpointIdAndInsAtBetween(checkpointId, start, end);
    }
}
