package com.wasel.backend.controller;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.model.Incident;
import com.wasel.backend.service.IncidentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Incident> create(@RequestBody Incident incident) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createIncident(incident));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyReportRequest request) {
        return ResponseEntity.ok(service.verifyReport(request));
    }

    // ✅ pagination بدل findAll
    @GetMapping
    public ResponseEntity<List<Incident>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getAllIncidents(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.getIncidentById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Incident> updateStatus(@PathVariable int id,
                                                 @RequestParam String status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Incident> close(@PathVariable int id) {
        return ResponseEntity.ok(service.closeIncident(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.deleteIncident(id);
        return ResponseEntity.noContent().build(); // ✅ 204
    }

    @GetMapping("/status")
    public ResponseEntity<List<Incident>> getByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getByStatus(status, page, size));
    }
}