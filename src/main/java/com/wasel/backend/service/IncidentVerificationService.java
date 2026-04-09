package com.wasel.backend.service;

import com.wasel.backend.dto.VerifyReportRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentVerificationService {

    private final IncidentService incidentService;
    private final InsertReportService reportService;
    private final UserService userService;
    private final CheckpointService checkpointService;
    private final CheckpointHistoryService historyService;

    public IncidentVerificationService(
            IncidentService incidentService,
            InsertReportService reportService,
            UserService userService,
            CheckpointService checkpointService,
            CheckpointHistoryService historyService) {

        this.incidentService = incidentService;
        this.reportService = reportService;
        this.userService = userService;
        this.checkpointService = checkpointService;
        this.historyService = historyService;
    }

    @Transactional
    public String verifyReport(VerifyReportRequest request) {

        // 1. Get data
        var report = reportService.getReport(request.getReportId());
        var moderator = userService.getModerator(request.getModeratorId());

        // 2. Validate
        reportService.validate(report);

        // 3. Create incident
        var incident = incidentService.create(report, moderator);

        // 4. Update report
        reportService.markAsVerified(report, incident);

        // 5. Log
        historyService.logVerification(report, moderator, incident);

        // 6. Handle checkpoint
        checkpointService.handleCheckpoint(report, incident);

        return "Verified successfully";
    }
}