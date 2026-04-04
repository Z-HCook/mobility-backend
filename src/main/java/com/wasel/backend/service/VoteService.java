 package com.wasel.backend.service;

import com.wasel.backend.dto.VoteRequest;
import com.wasel.backend.model.*;
import com.wasel.backend.repository.*;
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

    public String vote(VoteRequest request) {

        // validation 1: user exists
        if (!userRepo.existsById(request.userId)) {
            return "User not found";
        }

        // validation 2: report exists
        Report report = reportRepo.findById(request.reportId)
                .orElse(null);

        if (report == null) {
            return "Report not found";
        }

        // validation 3: vote type
        if (request.voteType != 1 && request.voteType != -1) {
            return "Vote must be +1 or -1";
        }

        // abuse prevention
        if (voteRepo.findByUserIdAndReportId(
                request.userId,
                request.reportId
        ).isPresent()) {
            return "User already voted";
        }

        // save vote
        ReportVote vote = new ReportVote();
        vote.setUserId(request.userId);
        vote.setReportId(request.reportId);
        vote.setVoteType(request.voteType);
        vote.setCreatedAt(LocalDateTime.now());

        vote = voteRepo.save(vote);

        // update credibility score
        List<ReportVote> votes =
                voteRepo.findByReportId(request.reportId);

        int total = 0;

        for (ReportVote v : votes) {
            total += v.getVoteType();
        }

        report.setCredibilityScore((float) total);
        reportRepo.save(report);

        // log history
        ReportModerationLog log = new ReportModerationLog();
        log.setReportId(request.reportId);
        log.setModeratorId(request.userId);
        log.setAction("VOTE");
        log.setNote("Vote type: " + request.voteType);
        log.setCreatedAt(LocalDateTime.now());

        logRepo.save(log);

        // save user activity
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