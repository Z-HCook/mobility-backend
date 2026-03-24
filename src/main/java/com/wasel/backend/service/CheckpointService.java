package com.wasel.backend.service;

import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.repository.CheckpointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckpointService {

    private final CheckpointRepository checkpointRepository;

    public CheckpointService(CheckpointRepository checkpointRepository) {
        this.checkpointRepository = checkpointRepository;
    }

    public List<Checkpoint> getAllCheckpoints() {
        return checkpointRepository.findAll();
    }

    public Checkpoint getCheckpointById(int id) {
        return checkpointRepository.findById(id).orElse(null);
    }

    public Checkpoint createCheckpoint(Checkpoint checkpoint) {
        return checkpointRepository.save(checkpoint);
    }

    public void deleteCheckpoint(int id) {
        checkpointRepository.deleteById(id);
    }
}