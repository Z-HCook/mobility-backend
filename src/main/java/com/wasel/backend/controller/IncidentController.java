package com.wasel.backend.controller;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.service.IncidentService;
import com.wasel.backend.usecase.VerifyReportUseCase;
import com.wasel.backend.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService service;
    private final RateLimitingService rateLimitingService;
    private final VerifyReportUseCase verifyReportUseCase;

    public IncidentController(VerifyReportUseCase verifyReportUseCase , IncidentService service, RateLimitingService rateLimitingService) {

        this.service = service;
        this.verifyReportUseCase = verifyReportUseCase;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyReportRequest verifyRequest, HttpServletRequest request) {

        Bucket bucket = rateLimitingService.resolveBucket(request.getRemoteAddr());

        if (bucket.tryConsume(1)) {
            String result = verifyReportUseCase.execute(verifyRequest);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many verification attempts. Please wait a minute before trying again.");
        }
    }
}
