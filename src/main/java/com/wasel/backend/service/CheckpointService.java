 package com.wasel.backend.service;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.exception.ResourceNotFoundException;
import com.wasel.backend.exception.UnauthorizedException;
import com.wasel.backend.model.*;
import com.wasel.backend.repository.CheckpointHistoryRepository;
import com.wasel.backend.repository.CheckpointRepository;
import com.wasel.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckpointService {

    private final CheckpointRepository checkpointRepository;
    private final UserRepository userRepository;
    private final CheckpointHistoryRepository checkpointHistoryRepository;

    public CheckpointService(CheckpointRepository checkpointRepository,
                             UserRepository userRepository,
                             CheckpointHistoryRepository checkpointHistoryRepository) {
        this.checkpointRepository = checkpointRepository;
        this.userRepository = userRepository;
        this.checkpointHistoryRepository = checkpointHistoryRepository;
    }


    @CacheEvict(value = "checkpoints", allEntries = true)
    public String insertCheckpoint(InsertCheckpointRequest request) {


        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        if (!"admin".equalsIgnoreCase(user.getRole())) {
            throw new UnauthorizedException("Only admins can add checkpoints");
        }


        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setName(request.getName());
        checkpoint.setLatitude(request.getLatitude());
        checkpoint.setLongitude(request.getLongitude());
        checkpoint.setDescription(request.getDescription());
        checkpoint.setCreatedBy(user);
        checkpoint.setCreatedAt(LocalDateTime.now());

        checkpointRepository.save(checkpoint);

        return "Checkpoint inserted successfully";
    }public List<CheckpointHistory> getByCheckpointId(Integer checkpointId) {
        List<CheckpointHistory> history =
                checkpointHistoryRepository.findByCheckpoint_Id(checkpointId);

        if (history.isEmpty()) {
            throw new ResourceNotFoundException("Checkpoint not found");
        }

        return history;
    }


    @Cacheable(value = "checkpointHistoryRange",
            key = "#checkpointId.toString() + #start.toString() + #end.toString()")
    public List<CheckpointHistory> getByCheckpointIdAndDate(
            Integer checkpointId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        if (checkpointId == null || checkpointId == 0) {
            throw new ResourceNotFoundException("Checkpoint ID is invalid");
        }


        if (!checkpointRepository.existsById(checkpointId)) {
            throw new ResourceNotFoundException("Checkpoint not found");
        }

        List<CheckpointHistory> history =
                checkpointHistoryRepository.findByCheckpoint_IdAndInsAtBetween(
                        checkpointId, start, end);


        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No data found for this checkpoint in the given date range");
        }

        return history;
    }


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

            checkpointHistoryRepository.save(history);
        }
    }
}