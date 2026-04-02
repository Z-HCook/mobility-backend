package com.wasel.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "vote_id")
    private Integer voteId;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserActivity() {}

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public Integer getVoteId() {
        return voteId;
    }

    public String getActionType() {
        return actionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public void setVoteId(Integer voteId) {
        this.voteId = voteId;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}