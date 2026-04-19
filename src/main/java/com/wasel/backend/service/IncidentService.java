package com.wasel.backend.service;
import com.wasel.backend.dto.RouteRequest;
import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.model.*;
import com.wasel.backend.repository.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepo;
    private final ReportRepository reportRepo;
    private final UserRepository userRepo;
    private final ReportModerationLogRepository logRepo;
    private final CheckpointRepository checkpointRepo;
    private final CheckpointHistoryRepository checkpointHistoryRepo;
    private final AlertService alertService;


    public IncidentService( ReportRepository reportRepo,
                            UserRepository userRepo,
                            IncidentRepository incidentRepo,
                            ReportModerationLogRepository logRepo,
                            CheckpointRepository checkpointRepo,
                            CheckpointHistoryRepository checkpointHistoryRepo,
                            IncidentRepository incidentRepository,
                            AlertService alertService )
    {
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
        this.incidentRepo = incidentRepo;
        this.logRepo = logRepo;
        this.checkpointRepo = checkpointRepo;
        this.checkpointHistoryRepo = checkpointHistoryRepo;
        this.alertService = alertService;

    }

    @Transactional
    public Incident createIncident(Incident incident) {
        incident.setCreatedAt(LocalDateTime.now());
        incident.setStatus("OPEN");
        return incidentRepo.save(incident);
    }
    public int countIncidentsNearRouteEndpoints(RouteRequest request) {
        final Incident incident = null;
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        List<Incident> incidents = incidentRepo.findRecentIncidents(thirtyMinutesAgo);
        Set<Incident> unique = new HashSet<>();

        for (var i : incidents) {


            double diststart = incident.distance(request.getStartLat(), request.getStartLng(),
                    i.getLatitude(), i.getLongitude());
            double distend = incident.distance(request.getEndLat(), request.getEndLng(),
                    i.getLatitude(), i.getLongitude());

            if (diststart < 3 || distend < 3) {
                unique.add(i);
            }
        }

        return unique.size();
    }


    @Transactional

    @Caching(evict = {
            @CacheEvict(value = "incidents", allEntries = true),
            @CacheEvict(value = "checkpointHistory", allEntries = true),
            @CacheEvict(value = "checkpointHistoryRange", allEntries = true)
    })
    public String verifyReport(VerifyReportRequest request) {


        Report report = reportRepo.findById(request.getReportId()).orElse(null);
        if (report == null) return "Report not found";


        if (!Boolean.TRUE.equals(report.getIsPromoted())) {
            return "Report is not eligible for verification";
        }


        User moderator = userRepo.findById(request.getModeratorId()).orElse(null);
        if (moderator == null) return "Moderator not found";


        if (!moderator.getRole().equalsIgnoreCase("admin")
                && !moderator.getRole().equalsIgnoreCase("moderator")) {
            return "Unauthorized";
        }


        if ("verified".equalsIgnoreCase(report.getStatus())) {
            return "Report already verified";
        }


        Incident incident = new Incident();
        incident.setTitle(report.getCategory());
        incident.setDescription(report.getDescription());
        incident.setType(mapCategoryToType(report.getCategory()));
        incident.setSeverity("medium");
        incident.setStatus("verified");
        incident.setLatitude(report.getLatitude());
        incident.setLongitude(report.getLongitude());
        incident.setReportedBy(report.getUserId());
        incident.setVerifiedBy(moderator.getId());
        incident.setCreatedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());
        incident.setCheckpointId(report.getLinkedCheckpointId());
        incidentRepo.save(incident);

        alertService.createAlertsForIncident(incident);




        report.setStatus("verified");
        report.setLinkedIncidentId(incident.getId());
        reportRepo.save(report);


        ReportModerationLog log = new ReportModerationLog();
        log.setReportId(report.getId());
        log.setModeratorId(moderator.getId());
        log.setAction("VERIFY");
        log.setNote("Created incident ID: " + incident.getId());
        log.setCreatedAt(LocalDateTime.now());
        logRepo.save(log);


        if (report.getLinkedCheckpointId() != null) {
            Checkpoint checkpoint = checkpointRepo.findById(report.getLinkedCheckpointId()).orElse(null);
            if (checkpoint != null) {
                CheckpointHistory history = new CheckpointHistory();
                history.setCheckpoint(checkpoint);
                history.setIncident(incident);
                history.setInsAt(LocalDateTime.now());
                checkpointHistoryRepo.save(history);
            }
        }

        return "Report verified and incident created";
    }


    private String mapCategoryToType(String category) {
        return switch (category.toLowerCase()) {
            case "traffic" -> "delay";
            case "safety" -> "accident";
            case "weather" -> "weather";
            default -> "closure";
        };
    }


    /*
    @Transactional
    public String verifyReport(VerifyReportRequest request) {
        Report report = reportService.getReport(request.getReportId());
        User moderator = userService.getModerator(request.getModeratorId());

        if (report.getLinkedCheckpointId() == null) {
            throw new IllegalArgumentException("Checkpoint is missing for this report");
        }
        if (request.getReportId() == null || request.getModeratorId() == null) {
            throw new IllegalArgumentException("reportId and moderatorId are required");
        }

        reportService.validate(report);

        Incident incident = new Incident();
        incident.setTitle(
                (report.getTitle() != null && !report.getTitle().isBlank())
                        ? report.getTitle()
                        : report.getCategory() + " incident"

        );
        incident.setDescription(report.getDescription());
        incident.setType(mapType(report.getCategory()));
        incident.setSeverity("MEDIUM");
        incident.setStatus("VERIFIED");

        incident.setReportedBy(report.getUserId());
        incident.setVerifiedBy(moderator.getId());

        incident.setLatitude(report.getLatitude());
        incident.setLongitude(report.getLongitude());
        incident.setCreatedAt(LocalDateTime.now());

        incident = repo.save(incident);

        reportService.markAsVerified(report, incident);
        historyService.logVerification(report, moderator, incident);
        checkpointService.handleCheckpoint(report, incident);

        return "Verified successfully";
    }
*/
    // ✅ pagination بدل findAll — هذا كان السبب الرئيسي للبطء
    @Cacheable(value = "incidents", key = "#page + '-' + #size")
    @Transactional(readOnly = true)
    public List<Incident> getAllIncidents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return incidentRepo.findAllByOrderByCreatedAtDesc(pageable).getContent();
    }
    @Transactional(readOnly = true)
    public Incident getIncidentById(int id) {
        return incidentRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + id));
    }

    @Transactional
    public void deleteIncident(int id) {
        if (!incidentRepo.existsById(id)) {
            throw new NoSuchElementException("Incident not found: " + id);
        }
        incidentRepo.deleteById(id);
    }

    @Transactional
    public Incident updateStatus(int id, String status) {
        Incident incident = getIncidentById(id);

        if (!List.of("OPEN", "CLOSED", "VERIFIED").contains(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }

        incident.setStatus(status); // 🔥 مهم
        return incidentRepo.save(incident);
    }

    @Transactional
    public Incident closeIncident(int id) {
        Incident incident = getIncidentById(id);
        incident.setStatus("CLOSED");
        incident.setClosedAt(LocalDateTime.now());
        return incidentRepo.save(incident);
    }

    @Transactional(readOnly = true)
    public List<Incident> getByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (!List.of("OPEN", "CLOSED", "VERIFIED").contains(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        return incidentRepo.findByStatusOrderByCreatedAtDesc(status, pageable).getContent();
    }

    private String mapType(String category) {
        return switch (category.toLowerCase()) {
            case "traffic" -> "DELAY";
            case "safety" -> "ACCIDENT";
            case "weather" -> "WEATHER";
            default -> "CLOSURE";
        };
    }
}