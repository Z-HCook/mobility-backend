package com.wasel.backend.service;

import com.wasel.backend.dto.VoteRequest;
import com.wasel.backend.model.*;
import com.wasel.backend.repository.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoteService {

    private final ReportVoteRepository voteRepo;
    private final ReportRepository reportRepo;
    private final UserRepository userRepo;
    private final ReportModerationLogRepository logRepo;
    private final UserActivityRepository activityRepo;

    public VoteService(
            ReportVoteRepository voteRepo,
            ReportRepository reportRepo,
            UserRepository userRepo,
            ReportModerationLogRepository logRepo,
            UserActivityRepository activityRepo
    ) {
        this.voteRepo = voteRepo;
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
        this.logRepo = logRepo;
        this.activityRepo = activityRepo;
    }

    @Caching(evict = {
            @CacheEvict(value = "reports", allEntries = true),
            @CacheEvict(value = "userActivities", key = "#request.userId")
    })
    public String vote(VoteRequest request) {

        String validationResult = validateRequest(request);
        if (validationResult != null) {
            return validationResult;
        }

        Report report = getReport(request.reportId);
        if (report == null) {
            return "Report not found";
        }

        if (isDuplicateVote(request)) {
            return "User already voted";
        }

        ReportVote vote = saveVote(request);

        updateReportScore(request.reportId, report);

        saveLog(request);

        saveUserActivity(request, vote);

        return "Vote added successfully";
    }

    private String validateRequest(VoteRequest request) {

        if (!userRepo.existsById(request.userId)) {
            return "User not found";
        }

        if (request.voteType != 1 && request.voteType != -1) {
            return "Vote must be +1 or -1";
        }

        return null;
    }

    private Report getReport(int reportId) {
        return reportRepo.findById(reportId).orElse(null);
    }

    private boolean isDuplicateVote(VoteRequest request) {
        return voteRepo.findByUserIdAndReportId(
                request.userId,
                request.reportId
        ).isPresent();
    }

    private ReportVote saveVote(VoteRequest request) {

        ReportVote vote = new ReportVote();
        vote.setUserId(request.userId);
        vote.setReportId(request.reportId);
        vote.setVoteType(request.voteType);
        vote.setCreatedAt(LocalDateTime.now());

        return voteRepo.save(vote);
    }

    private void updateReportScore(int reportId, Report report) {

        List<ReportVote> votes = voteRepo.findByReportId(reportId);

        int total = 0;

        for (ReportVote v : votes) {
            total += v.getVoteType();
        }

        report.setCredibilityScore((float) total);
        reportRepo.save(report);
    }

    private void saveLog(VoteRequest request) {

        ReportModerationLog log = new ReportModerationLog();
        log.setReportId(request.reportId);
        log.setModeratorId(request.userId);
        log.setAction("VOTE");
        log.setNote("Vote type: " + request.voteType);
        log.setCreatedAt(LocalDateTime.now());

        logRepo.save(log);
    }

    private void saveUserActivity(VoteRequest request, ReportVote vote) {

        UserActivity activity = new UserActivity();
        activity.setUserId(request.userId);
        activity.setReportId(request.reportId);
        activity.setVoteId(vote.getId());
        activity.setActionType("VOTE");
        activity.setCreatedAt(LocalDateTime.now());

        activityRepo.save(activity);
    }
}