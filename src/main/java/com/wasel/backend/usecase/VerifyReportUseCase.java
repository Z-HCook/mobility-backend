package com.wasel.backend.usecase;

import com.wasel.backend.dto.VerifyReportRequest;
import org.springframework.stereotype.Component;

@Component
public class VerifyReportUseCase {

    private final IncidentService incidentVerificationService;

    public VerifyReportUseCase(IncidentService incidentVerificationService) {
        this.incidentVerificationService = incidentVerificationService;
    }

    public String execute(VerifyReportRequest request) {
        return incidentVerificationService.verifyReport(request);
    }
}