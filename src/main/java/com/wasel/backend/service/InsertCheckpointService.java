package com.wasel.backend.service;

import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.CheckpointRepository;
import com.wasel.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InsertCheckpointService {

    private final CheckpointRepository checkpointRepository;
    private final UserRepository userRepository;

    public InsertCheckpointService(CheckpointRepository checkpointRepository, UserRepository userRepository) {
        this.checkpointRepository = checkpointRepository;
        this.userRepository = userRepository;
    }

    // ✅ مسح الكاش المسمى "checkpoints" عند إضافة حاجز جديد من قبل الآدمن
    @CacheEvict(value = "checkpoints", allEntries = true)
    public String insertCheckpoint(InsertCheckpointRequest request) {

        // جلب اليوزر
        Optional<User> userOpt = userRepository.findById(request.createdById);
        if (userOpt.isEmpty()) {
            return "User not found";
        }

        User user = userOpt.get();

        // تحقق أن الدور admin فقط
        if (!user.getRole().equalsIgnoreCase("admin")) {
            return "Only admins can add checkpoints";
        }

        // إنشاء الـ Checkpoint
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