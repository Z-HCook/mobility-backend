 package com.wasel.backend.service;
import com.wasel.backend.dto.VoteRequest;
import com.wasel.backend.exception.BusinessRuleException;
import com.wasel.backend.exception.ResourceNotFoundException;
import com.wasel.backend.exception.ValidationException;
import com.wasel.backend.model.*;
import com.wasel.backend.repository.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

        // 1. user check
        User user = userRepo.findById(request.userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. report check
        Report report = reportRepo.findById(request.reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        // 3. validation vote type
        if (request.voteType != 1 && request.voteType != -1) {
            throw new ValidationException("Vote must be +1 or -1");
        }

        // 4. already voted check
        if (voteRepo.findByUserIdAndReportId(request.userId, request.reportId).isPresent()) {
            throw new BusinessRuleException("User already voted");
        }

        // 5. save vote
        ReportVote vote = new ReportVote();
        vote.setUserId(request.userId);
        vote.setReportId(request.reportId);
        vote.setVoteType(request.voteType);
        vote.setCreatedAt(LocalDateTime.now());

        vote = voteRepo.save(vote);

        // 6. calculate score (simple version)
        int total = voteRepo.findByReportId(request.reportId)
                .stream()
                .mapToInt(ReportVote::getVoteType)
                .sum();

        report.setCredibilityScore((float) total);
        reportRepo.save(report);

        // 7. log moderation
        ReportModerationLog log = new ReportModerationLog();
        log.setReportId(request.reportId);
        log.setModeratorId(request.userId);
        log.setAction("VOTE");
        log.setNote("Vote type: " + request.voteType);
        log.setCreatedAt(LocalDateTime.now());
        logRepo.save(log);

        // 8. user activity
        UserActivity activity = new UserActivity();
        activity.setUserId(request.userId);
        activity.setReportId(request.reportId);
        activity.setVoteId(vote.getId());
        activity.setActionType("VOTE");
        activity.setCreatedAt(LocalDateTime.now());
        activityRepo.save(activity);

        return "Vote added successfully";
    }
}