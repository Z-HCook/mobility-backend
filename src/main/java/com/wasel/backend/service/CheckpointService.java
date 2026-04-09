package com.wasel.backend.service;
import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.model.*;
import com.wasel.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckpointService {

    private final CheckpointRepository checkpointRepository;
    private final UserRepository userRepository;
    private final CheckpointHistoryRepository historyRepository;

    public CheckpointService(CheckpointRepository checkpointRepository,
                             UserRepository userRepository,
                             CheckpointHistoryRepository historyRepository) {
        this.checkpointRepository = checkpointRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    public String createCheckpoint(CheckpointRequest request) {

        if (request.getName() == null || request.getName().isEmpty()) {
            throw new RuntimeException("Checkpoint name is required");
        }

        User user = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setName(request.getName());
        checkpoint.setLatitude(request.getLatitude());
        checkpoint.setLongitude(request.getLongitude());
        checkpoint.setDescription(request.getDescription());
        checkpoint.setCreatedBy(user);

        checkpointRepository.save(checkpoint);

        return "Checkpoint created successfully";
    }

    // ✅ SRP: هذا مسؤول فقط عن checkpoint logic
    public void handleCheckpoint(Report report, Incident incident) {

        if (report.getLinkedCheckpointId() == null) return;

        Checkpoint checkpoint = checkpointRepository
                .findById(report.getLinkedCheckpointId())
                .orElse(null);

        if (checkpoint != null) {
            CheckpointHistory history = new CheckpointHistory();
            history.setCheckpoint(checkpoint);
            history.setIncident(incident);
            history.setInsAt(LocalDateTime.now());

            historyRepository.save(history);
        }
    }
}