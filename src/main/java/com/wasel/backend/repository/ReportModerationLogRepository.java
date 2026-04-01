package com.wasel.backend.repository;

import com.wasel.backend.model.ReportModerationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportModerationLogRepository
        extends JpaRepository<ReportModerationLog, Integer> {

    long countByModeratorId(int moderatorId);
}