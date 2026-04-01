package com.wasel.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_moderation_logs")
public class ReportModerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "report_id")
    private int reportId;

    @Column(name = "moderator_id")
    private int moderatorId;

    private String action;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ReportModerationLog() {}

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}