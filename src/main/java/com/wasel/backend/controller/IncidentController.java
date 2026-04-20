package com.wasel.backend.controller;

import com.wasel.backend.dto.VerifyReportRequest;
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

    @PostMapping
    public Incident create(@RequestBody Incident incident) {
        return service.createIncident(incident);
    }

    @PostMapping("/verify")
    public String verify(@RequestBody VerifyReportRequest request) {
        return service.verifyReport(request);
    }

    @GetMapping
    public List<Incident> getAll() {
        return service.getAllIncidents();
    }

    @GetMapping("/{id}")
    public Incident getById(@PathVariable int id) {
        return service.getIncidentById(id);
    }

    @PutMapping("/{id}/status")
    public Incident updateStatus(@PathVariable int id,
                                 @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    @PutMapping("/{id}/close")
    public Incident close(@PathVariable int id) {
        return service.closeIncident(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.deleteIncident(id);
    }

    @GetMapping("/status")
    public List<Incident> getByStatus(@RequestParam String status) {
        return service.getByStatus(status);
    }
}