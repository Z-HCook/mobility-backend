package com.wasel.backend.repository;

import com.wasel.backend.model.CheckpointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckpointHistoryRepository extends JpaRepository<CheckpointHistory, Integer> {


    List<CheckpointHistory> findByCheckpoint_Id(Integer checkpointId);

    List<CheckpointHistory> findByCheckpoint_IdAndInsAtBetween(
            Integer checkpointId,
            LocalDateTime start,
            LocalDateTime end
    );
}