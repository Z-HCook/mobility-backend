package com.wasel.backend.service;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.CheckpointRepository;
import com.wasel.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
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

    // ✅ عند إنشاء حاجز جديد، نقوم بمسح الكاش المسمى "checkpoints"
    // لضمان أن المستخدم سيرى القائمة المحدثة في المرة القادمة.
    @CacheEvict(value = "checkpoints", allEntries = true)
    public String createCheckpoint(CheckpointRequest request) {

        validateRequest(request);

        User user = getUser(request.getCreatedBy());

        Checkpoint checkpoint = buildCheckpoint(request, user);

        checkpointRepository.save(checkpoint);

        return "Checkpoint created successfully";
    }

    private void validateRequest(CheckpointRequest request) {

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {
            throw new RuntimeException("Checkpoint name is required");
        }
    }

    private User getUser(int userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private Checkpoint buildCheckpoint(
            CheckpointRequest request,
            User user
    ) {

        Checkpoint checkpoint = new Checkpoint();

        checkpoint.setName(request.getName());
        checkpoint.setLatitude(request.getLatitude());
        checkpoint.setLongitude(request.getLongitude());
        checkpoint.setDescription(request.getDescription());
        checkpoint.setCreatedBy(user);

        return checkpoint;
    }
}