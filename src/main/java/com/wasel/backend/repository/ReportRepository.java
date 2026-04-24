package com.wasel.backend.repository;

import com.wasel.backend.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query(value = """
    SELECT * FROM reports r
    WHERE r.category = :category
    AND r.created_at BETWEEN :startTime AND :endTime
    LIMIT 50
    """, nativeQuery = true)
    List<Report> findSimilarReports(
            @Param("category") String category,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query(value = """
    SELECT * FROM reports r
    WHERE r.user_id = :userId
    AND r.category = :category
    AND r.created_at BETWEEN :startTime AND :endTime
    LIMIT 10
    """, nativeQuery = true)
    List<Report> findSimilarReportsByUser(
            @Param("userId") int userId,
            @Param("category") String category,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query(value = """
        SELECT COUNT(*) FROM report_votes rv
        WHERE rv.report_id = :reportId
        """, nativeQuery = true)
    int countVotes(@Param("reportId") int reportId);
}