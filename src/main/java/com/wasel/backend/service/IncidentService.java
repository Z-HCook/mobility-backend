package com.wasel.backend.service;

import com.wasel.backend.model.Incident;
import com.wasel.backend.model.IncidentStatus;
import com.wasel.backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository repository;

    public IncidentService(IncidentRepository repository) {
        this.repository = repository;
    }

    // ✅ Create
    public Incident createIncident(Incident incident) {
        incident.setCreatedAt(LocalDateTime.now());
        incident.setStatus(IncidentStatus.PENDING); // مهم
        return repository.save(incident);
    }

    // ✅ Get All
    public List<Incident> getAllIncidents() {
        return repository.findAll();
    }

    // ✅ Get By ID
    public Incident getIncidentById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
    }

    // ✅ Update (بدون status)
    public Incident updateIncident(int id, Incident updated) {
        Incident incident = getIncidentById(id);

        incident.setTitle(updated.getTitle());
        incident.setDescription(updated.getDescription());
        incident.setSeverity(updated.getSeverity());
        incident.setType(updated.getType());
        incident.setUpdatedAt(LocalDateTime.now());

        return repository.save(incident);
    }

    // ✅ Delete
    public void deleteIncident(int id) {
        repository.deleteById(id);
    }

    // ✅ Verify (Admin/Moderator)
    public Incident verifyIncident(int id, int adminId) {
        Incident incident = getIncidentById(id);

        incident.setStatus(IncidentStatus.VERIFIED);
        incident.setVerifiedBy(adminId);
        incident.setUpdatedAt(LocalDateTime.now());

        return repository.save(incident);
    }

    // ✅ Close
    public Incident closeIncident(int id) {
        Incident incident = getIncidentById(id);

        incident.setStatus(IncidentStatus.CLOSED);
        incident.setClosedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());

        return repository.save(incident);
    }
}