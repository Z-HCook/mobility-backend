package com.wasel.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_votes")
public class ReportVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "report_id")
    private int reportId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "vote_type")
    private int voteType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ReportVote() {}

    public int getId() {
        return id;
    }

    public int getReportId() {
        return reportId;
    }

    public int getUserId() {
        return userId;
    }

    public int getVoteType() {
        return voteType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setVoteType(int voteType) {
        this.voteType = voteType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}