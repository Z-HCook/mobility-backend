package com.wasel.backend.dto;

public class VerifyReportRequest {
    private Integer reportId;
    private Integer moderatorId;

    // constructors
    public VerifyReportRequest() {}

    public VerifyReportRequest(Integer reportId, Integer moderatorId) {
        this.reportId = reportId;
        this.moderatorId = moderatorId;
    }

    // getters & setters
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(Integer moderatorId) {
        this.moderatorId = moderatorId;
    }
}