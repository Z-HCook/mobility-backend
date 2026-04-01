package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.service.InsertReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insert")
public class InsertReportController {

    private final InsertReportService service;

    public InsertReportController(InsertReportService service) {
        this.service = service;
    }

    @PostMapping("/report")
    public String insertReport(@RequestBody InsertReportRequest request) {
        return service.insertReport(request);
    }
}