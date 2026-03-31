package com.wasel.backend.controller;

import com.wasel.backend.model.Incident;
import com.wasel.backend.service.IncidentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    // ✅ Create Incident
    @PostMapping
    public Incident createIncident(@RequestBody Incident incident) {
        return service.createIncident(incident);
    }

    // ✅ Get All Incidents
    @GetMapping
    public List<Incident> getAllIncidents() {
        return service.getAllIncidents();
    }

    // ✅ Get Incident by ID
    @GetMapping("/{id}")
    public Incident getIncidentById(@PathVariable int id) {
        return service.getIncidentById(id);
    }

    // ✅ Update Incident (بدون status)
    @PutMapping("/{id}")
    public Incident updateIncident(@PathVariable int id, @RequestBody Incident incident) {
        return service.updateIncident(id, incident);
    }

    // ✅ Delete Incident
    @DeleteMapping("/{id}")
    public void deleteIncident(@PathVariable int id) {
        service.deleteIncident(id);
    }

    // 🔥 Verify Incident (Admin/Moderator)
    @PutMapping("/{id}/verify")
    public Incident verifyIncident(
            @PathVariable int id,
            @RequestParam int adminId
    ) {
        return service.verifyIncident(id, adminId);
    }

    // 🔥 Close Incident
    @PutMapping("/{id}/close")
    public Incident closeIncident(@PathVariable int id) {
        return service.closeIncident(id);
    }
}