package com.wasel.backend.controller;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.service.IncidentVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentVerificationService service;

    public IncidentController(IncidentVerificationService service) {
        this.service = service;
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyReportRequest request) {
        return ResponseEntity.ok(service.verifyReport(request));
    }
}