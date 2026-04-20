package com.wasel.backend.service;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.model.Incident;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository repo;
    private final InsertReportService reportService;
    private final UserService userService;
    private final CheckpointService checkpointService;
    private final CheckpointHistoryService historyService;

    public IncidentService(IncidentRepository repo,
                           InsertReportService reportService,
                           UserService userService,
                           CheckpointService checkpointService,
                           CheckpointHistoryService historyService) {

        this.repo = repo;
        this.reportService = reportService;
        this.userService = userService;
        this.checkpointService = checkpointService;
        this.historyService = historyService;
    }

    // 🔹 CREATE مباشر
    public Incident createIncident(Incident incident) {
        incident.setCreatedAt(LocalDateTime.now());
        incident.setStatus("OPEN");
        return repo.save(incident);
    }

    // 🔹 VERIFY (🔥 دمجنا كل اللوجيك هون)
    public String verifyReport(VerifyReportRequest request) {

        Report report = reportService.getReport(request.getReportId());
        User moderator = userService.getModerator(request.getModeratorId());

        reportService.validate(report);

        Incident incident = new Incident();
        incident.setTitle(report.getTitle());
        incident.setDescription(report.getDescription());
        incident.setType(mapType(report.getCategory()));
        incident.setSeverity("MEDIUM");
        incident.setStatus("VERIFIED");

        incident.setReportedBy(report.getUserId());
        incident.setVerifiedBy(moderator.getId());

        incident.setLatitude(report.getLatitude());
        incident.setLongitude(report.getLongitude());

        repo.save(incident);

        reportService.markAsVerified(report, incident);
        historyService.logVerification(report, moderator, incident);
        checkpointService.handleCheckpoint(report, incident);

        return "Verified successfully";
    }

    // 🔹 بدل Mapper
    private String mapType(String category) {
        return switch (category.toLowerCase()) {
            case "traffic" -> "DELAY";
            case "safety" -> "ACCIDENT";
            case "weather" -> "WEATHER";
            default -> "CLOSURE";
        };
    }

    // 🔹 GET ALL
    public List<Incident> getAllIncidents() {
        return repo.findAll();
    }

    // 🔹 GET BY ID
    public Incident getIncidentById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
    }

    // 🔹 DELETE
    public void deleteIncident(int id) {
        repo.deleteById(id);
    }

    // 🔹 UPDATE STATUS
    public Incident updateStatus(int id, String status) {
        Incident incident = getIncidentById(id);
        incident.setStatus(status);
        return repo.save(incident);
    }

    // 🔹 CLOSE
    public Incident closeIncident(int id) {
        Incident incident = getIncidentById(id);
        incident.setStatus("CLOSED");
        incident.setClosedAt(LocalDateTime.now());
        return repo.save(incident);
    }

    // 🔹 FILTER
    public List<Incident> getByStatus(String status) {
        return repo.findByStatus(status);
    }
}