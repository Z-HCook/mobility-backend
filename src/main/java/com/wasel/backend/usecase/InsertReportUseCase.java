
package com.wasel.backend.usecase;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.service.InsertReportService;
import org.springframework.stereotype.Component;

@Component
public class InsertReportUseCase {

    private final InsertReportService insertReportService;

    public InsertReportUseCase(InsertReportService insertReportService) {
        this.insertReportService = insertReportService;
    }

    public String execute(InsertReportRequest request) {
        return insertReportService.insertReport(request);
    }
}