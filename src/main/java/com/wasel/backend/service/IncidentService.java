package com.wasel.backend.service;

import com.wasel.backend.model.Incident;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;
import com.wasel.backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public Incident create(Report report, User moderator) {

        Incident incident = new Incident();

        incident.setTitle(report.getCategory());
        incident.setDescription(report.getDescription());
        incident.setStatus("verified");
        incident.setLatitude(report.getLatitude());
        incident.setLongitude(report.getLongitude());
        incident.setReportedBy(report.getUserId());
        incident.setVerifiedBy(moderator.getId());

        return incidentRepository.save(incident);
    }
}