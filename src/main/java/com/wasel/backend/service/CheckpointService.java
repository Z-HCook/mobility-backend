package com.wasel.backend.service;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.CheckpointRepository;
import com.wasel.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CheckpointService {

    private final CheckpointRepository checkpointRepository;
    private final UserRepository userRepository;

    public CheckpointService(CheckpointRepository checkpointRepository,
                             UserRepository userRepository) {
        this.checkpointRepository = checkpointRepository;
        this.userRepository = userRepository;
    }

    public String createCheckpoint(CheckpointRequest request) {

        // ✅ validation
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
}