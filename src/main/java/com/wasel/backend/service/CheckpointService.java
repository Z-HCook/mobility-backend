 package com.wasel.backend.service;

import com.wasel.backend.dto.CheckpointRequest;
import com.wasel.backend.dto.InsertCheckpointRequest;
import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.model.CheckpointHistory;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.CheckpointHistoryRepository;
import com.wasel.backend.repository.CheckpointRepository;
import com.wasel.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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


        Optional<User> userOpt = userRepository.findById(request.createdById);
        if (userOpt.isEmpty()) {
            return "User not found";
        }

        User user = userOpt.get();


        if (!user.getRole().equalsIgnoreCase("admin")) {
            return "Only admins can add checkpoints";
        }


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

 /*   @CacheEvict(value = "checkpoints", allEntries = true)
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
        return "successful";
    }
*/




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
        if(checkpointId ==0 ||checkpointId ==null  )
        {

        }
        return checkpointHistoryRepository.findByCheckpoint_IdAndInsAtBetween(
                checkpointId, start, end);

    }
}