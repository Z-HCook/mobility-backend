package com.wasel.backend.service.impl;

import com.wasel.backend.model.Incident;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.IncidentRepository;
import com.wasel.backend.service.IncidentServiceint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IncidentServiceImpl implements IncidentServiceint {

    private final IncidentRepository incidentRepository;

    public IncidentServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Override
    public Incident create(Report report, User moderator) {

        Incident incident = new Incident();

        incident.setTitle(report.getCategory());
        incident.setDescription(report.getDescription());
        incident.setType(mapCategoryToType(report.getCategory()));

        incident.setSeverity("MEDIUM");
        incident.setStatus("VERIFIED");

        incident.setLatitude(report.getLatitude());
        incident.setLongitude(report.getLongitude());

        incident.setReportedBy(report.getUserId());
        incident.setVerifiedBy(moderator.getId());

        incident.setCreatedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());

        return incidentRepository.save(incident);
    }

    private String mapCategoryToType(String category) {
        return switch (category.toLowerCase()) {
            case "traffic" -> "DELAY";
            case "safety" -> "ACCIDENT";
            case "weather" -> "WEATHER";
            default -> "CLOSURE";
        };
    }
}