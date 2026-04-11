package com.wasel.backend.service;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.model.Incident;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentVerificationService {

    private final IncidentServiceint incidentService;
    private final InsertReportService reportService;
    private final UserService userService;
    private final CheckpointService checkpointService;
    private final CheckpointHistoryService historyService;

    public IncidentVerificationService(
            IncidentServiceint incidentService,
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

        // 1. جلب البيانات
        Report report = reportService.getReport(request.getReportId());
        User moderator = userService.getModerator(request.getModeratorId());

        // 2. التحقق من صحة التقرير
        reportService.validate(report);

        // 3. إنشاء Incident
        Incident incident = incidentService.create(report, moderator);

        // 4. تحديث التقرير بعد التحقق
        reportService.markAsVerified(report, incident);

        // 5. تسجيل عملية التحقق (Audit log)
        historyService.logVerification(report, moderator, incident);

        // 6. التعامل مع checkpoint
        checkpointService.handleCheckpoint(report, incident);

        // 7. النتيجة النهائية
        return "Verified successfully";
    }
}