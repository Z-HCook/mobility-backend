package com.wasel.backend.service;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.model.Incident;
import com.wasel.backend.model.Report;
import com.wasel.backend.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InsertReportService {

    private final ReportRepository reportRepository;
    private final ReportValidationService validationService;
    private final ReportDuplicateService duplicateService;

    public InsertReportService(ReportRepository reportRepository,
                               ReportValidationService validationService,
                               ReportDuplicateService duplicateService) {
        this.reportRepository = reportRepository;
        this.validationService = validationService;
        this.duplicateService = duplicateService;
    }

    @Transactional
    public String insertReport(InsertReportRequest request) {

        // 1️⃣ validate user
        validationService.validateUser(request.userId);

        LocalDateTime now = LocalDateTime.now();

        // 2️⃣ create report
        Report report = new Report();
        report.setUserId(request.userId);
        report.setCategory(request.category);
        report.setDescription(request.description);
        report.setLatitude(request.latitude);
        report.setLongitude(request.longitude);
        report.setType(request.type);

        report.setStatus("pending");
        report.setCredibilityScore(0f);
        report.setIsPromoted(false);
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        // 3️⃣ handle duplicate
        duplicateService.handleDuplicate(report, now);

        // 4️⃣ save
        reportRepository.save(report);

        return "Report inserted successfully";
    }

    public Report getReport(Integer id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public void validate(Report report) {
        if (!Boolean.TRUE.equals(report.getIsPromoted())) {
            throw new RuntimeException("Report is not eligible");
        }

        if ("verified".equalsIgnoreCase(report.getStatus())) {
            throw new RuntimeException("Already verified");
        }
    }

    public void markAsVerified(Report report, Incident incident) {
        report.setStatus("verified");
        report.setLinkedIncidentId(incident.getId());
        reportRepository.save(report);
    }
}