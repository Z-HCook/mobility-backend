package com.wasel.backend.controller;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.usecase.VerifyReportUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final VerifyReportUseCase verifyReportUseCase;

    public IncidentController(VerifyReportUseCase verifyReportUseCase) {
        this.verifyReportUseCase = verifyReportUseCase;
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyReportRequest request) {
        String result = verifyReportUseCase.execute(request);
        return ResponseEntity.ok(result);
    }
}