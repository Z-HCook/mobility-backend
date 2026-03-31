package com.wasel.backend.repository;

import com.wasel.backend.model.CheckpointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface CheckpointHistoryRepository extends JpaRepository<CheckpointHistory, Integer> {


    List<CheckpointHistory> findByCheckpointId(Integer checkpointId);


    List<CheckpointHistory> findByCheckpointIdAndInsAtBetween(
            Integer checkpointId,
            LocalDateTime start,
            LocalDateTime end
    );
}
