package com.wasel.backend.controller;

import com.wasel.backend.model.Incident;
import com.wasel.backend.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // ✅ إنشاء حادث جديد واستدعاء السيرفس
    @PostMapping("/create")
    public ResponseEntity<?> createIncident(@RequestBody Incident incident) {
        try {
            // استدعاء الميثود من السيرفس
            Incident saved = incidentService.createAndNotify(incident);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}