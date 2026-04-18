 package com.wasel.backend.service;

import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.exception.ResourceNotFoundException;
import com.wasel.backend.exception.UnauthorizedException;
import com.wasel.backend.exception.ValidationException;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.CheckpointRepository;
import com.wasel.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InsertCheckpointService {

    private final CheckpointRepository checkpointRepository;
    private final UserRepository userRepository;

    public InsertCheckpointService(CheckpointRepository checkpointRepository, UserRepository userRepository) {
        this.checkpointRepository = checkpointRepository;
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "checkpoints", allEntries = true)
    public String insertCheckpoint(InsertCheckpointRequest request) {

        if (request.name == null || request.name.isBlank())
            throw new ValidationException("Checkpoint name is required");

        User user = userRepository.findById(request.createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().equalsIgnoreCase("admin"))
            throw new UnauthorizedException("Only admins can add checkpoints");

        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setName(request.name);
        checkpoint.setLatitude(request.latitude);
        checkpoint.setLongitude(request.longitude);
        checkpoint.setDescription(request.description);
        checkpoint.setCreatedBy(user);
        checkpoint.setCreatedAt(LocalDateTime.now());

        checkpointRepository.save(checkpoint);

        return "Checkpoint inserted successfully";
    }
}