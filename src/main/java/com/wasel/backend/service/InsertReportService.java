package com.wasel.backend.service;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;
import com.wasel.backend.model.UserActivity;
import com.wasel.backend.repository.ReportRepository;
import com.wasel.backend.repository.UserActivityRepository;
import com.wasel.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InsertReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final UserActivityRepository activityRepository;

    public InsertReportService(
            ReportRepository reportRepository,
            UserRepository userRepository,
            UserActivityRepository activityRepository
    ) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private Report getRootReport(Report report) {
        while (report.getDuplicateOf() != null) {
            report = reportRepository
                    .findById(report.getDuplicateOf())
                    .orElse(report);
        }
        return report;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reports", allEntries = true),
            @CacheEvict(value = "userActivities", key = "#request.userId")
    })
    public String insertReport(InsertReportRequest request) {

        String validationResult = validateUser(request.userId);
        if (validationResult != null) {
            return validationResult;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(30);
        LocalDateTime end = now.plusMinutes(30);

        if (hasRecentDuplicateFromSameUser(request, start, end)) {
            return "You already reported this recently";
        }

        Report matchedReport = findMatchedReport(request, start, end);

        String activityCheck = checkUserActivity(request.userId, matchedReport);
        if (activityCheck != null) {
            return activityCheck;
        }

        Report report = buildReport(request, now);

        if (matchedReport != null) {
            updateRootReport(matchedReport);
            report.setDuplicateOf(matchedReport.getId());
        } else {
            report.setDuplicateCount(0);
        }

        report = reportRepository.save(report);

        saveActivity(request.userId, report.getId());

        return "Report inserted successfully";
    }

    private String validateUser(int userId) {

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return "User not found";
        }

        return null;
    }

    private boolean hasRecentDuplicateFromSameUser(
            InsertReportRequest request,
            LocalDateTime start,
            LocalDateTime end
    ) {

        List<Report> recentUserReports =
                reportRepository.findSimilarReportsByUser(
                        request.userId,
                        request.category,
                        start,
                        end
                );

        for (Report r : recentUserReports) {
            double dist = distance(
                    request.latitude,
                    request.longitude,
                    r.getLatitude(),
                    r.getLongitude()
            );

            if (dist < 0.2) {
                return true;
            }
        }

        return false;
    }

    private Report findMatchedReport(
            InsertReportRequest request,
            LocalDateTime start,
            LocalDateTime end
    ) {

        List<Report> candidates =
                reportRepository.findSimilarReports(
                        request.category,
                        start,
                        end
                );

        for (Report r : candidates) {
            double dist = distance(
                    request.latitude,
                    request.longitude,
                    r.getLatitude(),
                    r.getLongitude()
            );

            if (dist < 0.2) {
                return getRootReport(r);
            }
        }

        return null;
    }

    private String checkUserActivity(int userId, Report matchedReport) {

        if (matchedReport != null) {
            int activityCount =
                    activityRepository.countByUserId(userId);

            if (activityCount < 5) {
                return "User is not active enough to create reports";
            }
        }

        return null;
    }

    private Report buildReport(
            InsertReportRequest request,
            LocalDateTime now
    ) {

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

        return report;
    }

    private void updateRootReport(Report root) {

        int duplicates = root.getDuplicateCount() + 1;

        int votes = reportRepository.countVotes(root.getId());

        double newScore = (duplicates * 1.0) + (votes * 0.7);

        root.setDuplicateCount(duplicates);
        root.setCredibilityScore((float) newScore);

        if (newScore >= 15) {
            root.setIsPromoted(true);
        }

        reportRepository.save(root);
    }

    private void saveActivity(int userId, int reportId) {

        UserActivity activity = new UserActivity();

        activity.setUserId(userId);
        activity.setReportId(reportId);
        activity.setActionType("REPORT_CREATED");
        activity.setCreatedAt(LocalDateTime.now());

        activityRepository.save(activity);
    }
}