package com.wasel.backend.service;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.ReportRepository;
import com.wasel.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InsertReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public InsertReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public String insertReport(InsertReportRequest request) {

        // ✅ check user exists
        Optional<User> userOpt = userRepository.findById(request.userId);
        if (userOpt.isEmpty()) {
            return "User not found";
        }

        // ✅ create report
        Report report = new Report();
        report.setUserId(request.userId);
        report.setCategory(request.category);
        report.setDescription(request.description);
        report.setLatitude(request.latitude);
        report.setLongitude(request.longitude);
        report.setType(request.type);

        // defaults
        report.setStatus("pending");
        report.setCredibilityScore(0f);
        report.setIsPromoted(false);

        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());

        reportRepository.save(report);

        return "Report inserted successfully";
    }
}