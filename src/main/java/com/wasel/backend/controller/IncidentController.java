package com.wasel.backend.controller;

import com.wasel.backend.model.Incident;
import com.wasel.backend.service.IncidentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    // ✅ Create
    @PostMapping
    public Incident createIncident(@RequestBody Incident incident) {
        return service.createIncident(incident);
    }

    // ✅ Get All
    @GetMapping
    public List<Incident> getAllIncidents() {
        return service.getAllIncidents();
    }

    // ✅ Get By Id
    @GetMapping("/{id}")
    public Incident getIncidentById(@PathVariable int id) {
        return service.getIncidentById(id);
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public void deleteIncident(@PathVariable int id) {
        service.deleteIncident(id);
    }
}