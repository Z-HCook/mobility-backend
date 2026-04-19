
package com.wasel.backend.usecase;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.service.ReportService;
import org.springframework.stereotype.Component;

@Component
public class InsertReportUseCase {

    private final ReportService insertReportService;

    public InsertReportUseCase(ReportService insertReportService) {
        this.insertReportService = insertReportService;
    }

    public String execute(InsertReportRequest request) {
        return insertReportService.insertReport(request);
    }
}