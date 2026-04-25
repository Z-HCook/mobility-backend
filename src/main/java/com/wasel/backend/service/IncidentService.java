package com.wasel.backend.service;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.model.Incident;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.IncidentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository repo;
    private final ReportService reportService;
    private final UserService userService;
    private final CheckpointService checkpointService;
    private final CheckpointService historyService;

    public IncidentService(IncidentRepository repo,
                           ReportService reportService,
                           UserService userService,
                           CheckpointService checkpointService,
                           CheckpointService historyService) {
        this.repo = repo;
        this.reportService = reportService;
        this.userService = userService;
        this.checkpointService = checkpointService;
        this.historyService = historyService;
    }

    @Transactional
    public Incident createIncident(Incident incident) {
        incident.setCreatedAt(LocalDateTime.now());
        incident.setStatus("OPEN");
        return repo.save(incident);
    }

    @Transactional
    public String verifyReport(VerifyReportRequest request) {
        try {

            Report report = reportService.getReport(request.getReportId());
            User moderator = userService.getModerator(request.getModeratorId());

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

            repo.save(incident);

            reportService.markAsVerified(report, incident);
            historyService.logVerification(report, moderator, incident);
            checkpointService.handleCheckpoint(report, incident);

            return "Verified successfully";

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Verification failed: " + e.getMessage());
        }
    }

    // ✅ pagination بدل findAll — هذا كان السبب الرئيسي للبطء
    @Transactional(readOnly = true)
    public List<Incident> getAllIncidents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAllByOrderByCreatedAtDesc(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public Incident getIncidentById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found: " + id));
    }

    @Transactional
    public void deleteIncident(int id) {
        if (!repo.existsById(id)) throw new RuntimeException("Incident not found: " + id);
        repo.deleteById(id);
    }

    @Transactional
    public Incident updateStatus(int id, String status) {
        Incident incident = getIncidentById(id);
        incident.setStatus(status);
        return repo.save(incident);
    }

    @Transactional
    public Incident closeIncident(int id) {
        Incident incident = getIncidentById(id);
        incident.setStatus("CLOSED");
        incident.setClosedAt(LocalDateTime.now());
        return repo.save(incident);
    }

    @Transactional(readOnly = true)
    public List<Incident> getByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByStatusOrderByCreatedAtDesc(status, pageable).getContent();
    }

    private String mapType(String category) {
        return switch (category.toLowerCase()) {
            case "traffic" -> "DELAY";
            case "safety"  -> "ACCIDENT";
            case "weather" -> "WEATHER";
            default        -> "CLOSURE";
        };
    }


}