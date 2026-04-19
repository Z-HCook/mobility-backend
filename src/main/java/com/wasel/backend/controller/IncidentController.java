package com.wasel.backend.controller;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.model.Incident;
import com.wasel.backend.service.IncidentService;
import com.wasel.backend.service.RateLimitingService;
import com.wasel.backend.usecase.VerifyReportUseCase;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService service;
    private final  RateLimitingService rateLimitingService;
   private final VerifyReportUseCase verifyReportUseCase;
    public IncidentController(IncidentService service, RateLimitingService rateLimitingService, VerifyReportUseCase verifyReportUseCase) {
        this.service = service;
        this.rateLimitingService = rateLimitingService;
        this.verifyReportUseCase = verifyReportUseCase;
    }

    @PostMapping
    public ResponseEntity<String> verify(@RequestBody VerifyReportRequest verifyRequest, HttpServletRequest request) {


        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {

            String result =verifyReportUseCase.execute(verifyRequest);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many verification attempts. Please wait a minute before trying again.");
        }
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