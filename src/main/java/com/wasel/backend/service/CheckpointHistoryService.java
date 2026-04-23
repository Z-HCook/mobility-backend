package com.wasel.backend.service;

import com.wasel.backend.model.*;
import com.wasel.backend.repository.CheckpointHistoryRepository;
import com.wasel.backend.repository.CheckpointRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckpointHistoryService {

    private final CheckpointHistoryRepository checkpointHistoryRepository;
    private final CheckpointRepository checkpointRepository;

    public CheckpointHistoryService(
            CheckpointHistoryRepository checkpointHistoryRepository,
            CheckpointRepository checkpointRepository) {

        this.checkpointHistoryRepository = checkpointHistoryRepository;
        this.checkpointRepository = checkpointRepository;
    }

    // 📌 Get all history for a checkpoint
    public List<CheckpointHistory> getByCheckpointId(Integer checkpointId) {
        return checkpointHistoryRepository.findByCheckpoint_Id(checkpointId);
    }

    // 📌 Get history by date range
    public List<CheckpointHistory> getByCheckpointIdAndDate(
            Integer checkpointId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return checkpointHistoryRepository
                .findByCheckpoint_IdAndInsAtBetween(checkpointId, start, end);
    }

    // 🔥 Log verification event
    public void logVerification(Report report, User moderator, Incident incident) {

        if (report.getLinkedCheckpointId() == null) {
            return;
        }

        Checkpoint checkpoint = checkpointRepository
                .findById(report.getLinkedCheckpointId())
                .orElseThrow(() -> new RuntimeException("Checkpoint not found"));

        CheckpointHistory history = new CheckpointHistory();
        history.setCheckpoint(checkpoint);
        history.setIncident(incident);
        history.setInsAt(LocalDateTime.now());

        checkpointHistoryRepository.save(history);
    }
}