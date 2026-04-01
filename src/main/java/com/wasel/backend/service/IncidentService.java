package com.wasel.backend.service;

import com.wasel.backend.model.Incident;
import com.wasel.backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository repo;

    public IncidentService(IncidentRepository repo) {
        this.repo = repo;
    }

    public Incident createIncident(Incident incident) {
        return repo.save(incident);
    }

    public List<Incident> getAllIncidents() {
        return repo.findAll();
    }

    public Incident getIncidentById(int id) {
        return repo.findById(id).orElse(null);
    }

    public void deleteIncident(int id) {
        repo.deleteById(id);
    }
}