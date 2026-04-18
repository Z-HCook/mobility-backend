package com.wasel.backend.usecase;

import com.wasel.backend.dto.VerifyReportRequest;
import org.springframework.stereotype.Component;

@Component
public class VerifyReportUseCase {

    private final IncidentVerificationService incidentVerificationService;

    public VerifyReportUseCase(IncidentVerificationService incidentVerificationService) {
        this.incidentVerificationService = incidentVerificationService;
    }

    public String execute(VerifyReportRequest request) {
        return incidentVerificationService.verifyReport(request);
    }
}