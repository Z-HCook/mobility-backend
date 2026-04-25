package com.wasel.backend.service;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.exception.BusinessRuleException;
import com.wasel.backend.exception.ResourceNotFoundException;
import com.wasel.backend.model.*;
import com.wasel.backend.repository.ReportRepository;
import com.wasel.backend.repository.UserActivityRepository;
import com.wasel.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final UserActivityRepository activityRepository;


    public ReportService(
            ReportRepository reportRepository,
            UserRepository userRepository,
            UserActivityRepository activityRepository
    ) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    private Report getRootReport(Report report) {
        while (report.getDuplicateOf() != null) {
            report = reportRepository.findById(report.getDuplicateOf()).orElse(report);
        }
        return report;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reports", allEntries = true),
            @CacheEvict(value = "userActivities", key = "#request.userId")
    })
    public String insertReport(InsertReportRequest request) {

        getUserOrThrow(request.userId);


        validateNoDuplicateFromSameUser(request);

        Report matchedReport = findMatchingReport(request);
        Report report        = buildReport(request);

        handleDuplicateLogic(report, matchedReport);
        saveReportAndActivity(report);

        return "Report inserted successfully";
    }

    @Cacheable(value = "users", key = "#userId")
    private User getUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with id " + userId + " not found")
                );
    }

    private void validateNoDuplicateFromSameUser(InsertReportRequest request) {
        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(30);
        LocalDateTime end   = now.plusMinutes(30);

        List<Report> reports = reportRepository.findSimilarReportsByUser(
                request.userId, request.category, start, end
        );

        for (Report r : reports) {
            if (isNear(request.latitude, request.longitude, r)) {
                throw new BusinessRuleException("You already reported this recently");
            }
        }
    }

    private Report findMatchingReport(InsertReportRequest request) {
        LocalDateTime now   = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(30);
        LocalDateTime end   = now.plusMinutes(30);

        List<Report> candidates = reportRepository.findSimilarReports(
                request.category, start, end
        );

        for (Report r : candidates) {
            if (isNear(request.latitude, request.longitude, r)) {
                return getRootReport(r);
            }
        }
        return null;
    }

    private Report buildReport(InsertReportRequest request) {
        LocalDateTime now = LocalDateTime.now();

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
        report.setLinkedCheckpointId(request.linkedcheckpoint);

        return report;
    }



    private void handleDuplicateLogic(Report report, Report matchedReport) {
        if (matchedReport == null) {
            report.setDuplicateCount(0);
            return;
        }

        report.setDuplicateOf(matchedReport.getId());
        report.setDuplicateCount(0);

        int    duplicates = matchedReport.getDuplicateCount() + 1;
        int    votes      = reportRepository.countVotes(matchedReport.getId());
        double newScore   = (duplicates * 1.0) + (votes * 0.7);

        matchedReport.setDuplicateCount(duplicates);
        matchedReport.setCredibilityScore((float) newScore);

        if (newScore >= 15) matchedReport.setIsPromoted(true);

        reportRepository.save(matchedReport);
    }

    private void saveReportAndActivity(Report report) {
        report = reportRepository.save(report);

        UserActivity activity = new UserActivity();
        activity.setUserId(report.getUserId());
        activity.setReportId(report.getId());
        activity.setActionType("REPORT_CREATED");
        activity.setCreatedAt(LocalDateTime.now());

        activityRepository.save(activity);
    }



    private boolean isNear(double lat1, double lon1, Report r) {
        double dist = haversineDistance(lat1, lon1, r.getLatitude(), r.getLongitude());
        return dist < 0.2;
    }

    private static double haversineDistance(double lat1, double lon1,
                                            double lat2, double lon2) {
        final double R    = 6371.0;
        double       dLat = Math.toRadians(lat2 - lat1);
        double       dLon = Math.toRadians(lon2 - lon1);
        double       a    = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public Report getReport(Integer reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Report not found: " + reportId)
                );
    }

    public void validate(Report report) {
        if (!"pending".equalsIgnoreCase(report.getStatus())) {
            throw new BusinessRuleException("Report already verified or processed");
        }
    }

    @jakarta.transaction.Transactional
    public void markAsVerified(Report report, Incident incident) {
        report.setStatus("verified");
        report.setUpdatedAt(LocalDateTime.now());


        report.setLinkedIncidentId(incident.getId());

        reportRepository.save(report);
    }
}