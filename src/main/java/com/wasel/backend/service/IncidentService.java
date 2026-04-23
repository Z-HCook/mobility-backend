package com.wasel.backend.service;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.model.Incident;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.IncidentRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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

    @Transactional
    public Incident createIncident(Incident incident) {
        incident.setCreatedAt(LocalDateTime.now());
        incident.setStatus("OPEN");
        return repo.save(incident);
    }
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

    // ✅ pagination بدل findAll — هذا كان السبب الرئيسي للبطء
    @Cacheable(value = "incidents", key = "#page + '-' + #size")
    @Transactional(readOnly = true)
    public List<Incident> getAllIncidents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAllByOrderByCreatedAtDesc(pageable).getContent();
    }
    @Transactional(readOnly = true)
    public Incident getIncidentById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Incident not found: " + id));
    }

    @Transactional
    public void deleteIncident(int id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Incident not found: " + id);
        }
        repo.deleteById(id);
    }

    @Transactional
    public Incident updateStatus(int id, String status) {
        Incident incident = getIncidentById(id);

        if (!List.of("OPEN", "CLOSED", "VERIFIED").contains(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }

        incident.setStatus(status); // 🔥 مهم
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
        if (!List.of("OPEN", "CLOSED", "VERIFIED").contains(status)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        return repo.findByStatusOrderByCreatedAtDesc(status, pageable).getContent();
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