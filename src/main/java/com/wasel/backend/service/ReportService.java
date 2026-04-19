
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
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.awt.geom.Point2D.distance;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final UserActivityRepository activityRepository;
    Incident incident =null;


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
/*
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reports", allEntries = true),
            @CacheEvict(value = "userActivities", key = "#request.userId")
    })
    public String insertReport(InsertReportRequest request) {
        final Checkpoint checkpoint = null;
        final Incident incident = null;

        User user = userRepository.findById(request.userId)

                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + request.userId + " not found"
                ));


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(30);
        LocalDateTime end = now.plusMinutes(30);

        List<Report> recentUserReports = reportRepository.findSimilarReportsByUser(
                request.userId, request.category, start, end
        );

        for (Report r : recentUserReports) {
            double dist = incident.distance(
                    request.latitude,
                    request.longitude,
                    r.getLatitude(),
                    r.getLongitude()
            );

            if (dist < 0.2) {
                throw new BusinessRuleException("You already reported this recently");
            }
        }

        List<Report> candidates = reportRepository.findSimilarReports(
                request.category, start, end
        );

        Report matchedReport = null;

        for (Report r : candidates) {
            double dist = incident.distance(
                    request.latitude,
                    request.longitude,
                    r.getLatitude(),
                    r.getLongitude()
            );

            if (dist < 0.2) {
                matchedReport = getRootReport(r);
                break;
            }
        }

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

        if (matchedReport != null) {

            Report root = matchedReport;

            report.setDuplicateOf(root.getId());

            int duplicates = root.getDuplicateCount() + 1;

            int votes = reportRepository.countVotes(root.getId());

            double newScore = (duplicates * 1.0) + (votes * 0.7);

            root.setDuplicateCount(duplicates);
            root.setCredibilityScore((float) newScore);

            if (newScore >= 15) {
                root.setIsPromoted(true);
            }
            int activityCount = activityRepository.countByUserId(request.userId);

            if (activityCount < 5) {
                return "User is not active enough to create reports";
            }
            else {
                reportRepository.save(root);
            }
        } else {
            report.setDuplicateCount(0);
        }

        report = reportRepository.save(report);

        UserActivity activity = new UserActivity();
        activity.setUserId(request.userId);
        activity.setReportId(report.getId());
        activity.setActionType("REPORT_CREATED");
        activity.setCreatedAt(LocalDateTime.now());

        activityRepository.save(activity);

        return "Report inserted successfully";
    }*/

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reports", allEntries = true),
            @CacheEvict(value = "userActivities", key = "#request.userId")
    })
    public String insertReport(InsertReportRequest request) {

        User user = getUserOrThrow(request.userId);

        validateUserActivity(request.userId);

        validateNoDuplicateFromSameUser(request);

        Report matchedReport = findMatchingReport(request);

        Report report = buildReport(request);

        handleDuplicateLogic(report, matchedReport);

        saveReportAndActivity(report);

        return "Report inserted successfully";
    }

    private User getUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with id " + userId + " not found")
                );
    }

    private void validateUserActivity(Integer userId) {
        int activityCount = activityRepository.countByUserId(userId);

        if (activityCount < 5) {
            throw new BusinessRuleException("User is not active enough to create reports");
        }
    }



    private void validateNoDuplicateFromSameUser(InsertReportRequest request) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(30);
        LocalDateTime end = now.plusMinutes(30);

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

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(30);
        LocalDateTime end = now.plusMinutes(30);

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

        int duplicates = matchedReport.getDuplicateCount() + 1;
        int votes = reportRepository.countVotes(matchedReport.getId());

        double newScore = (duplicates * 1.0) + (votes * 0.7);

        matchedReport.setDuplicateCount(duplicates);
        matchedReport.setCredibilityScore((float) newScore);

        if (newScore >= 15) {
            matchedReport.setIsPromoted(true);
        }

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

        double dist = incident.distance(lat1, lon1, r.getLatitude(), r.getLongitude());
        return dist < 0.2;
    }

}