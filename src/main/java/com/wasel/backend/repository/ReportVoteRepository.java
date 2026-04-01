package com.wasel.backend.repository;

import com.wasel.backend.model.ReportVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReportVoteRepository extends JpaRepository<ReportVote, Integer> {

    List<ReportVote> findByReportId(int reportId);

    Optional<ReportVote> findByUserIdAndReportId(int userId, int reportId);

    long countByUserId(int userId);
}
