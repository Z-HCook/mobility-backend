package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.service.InsertReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class InsertReportController {

    private final InsertReportService service;

    public InsertReportController(InsertReportService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<?> insertReport(@RequestBody InsertReportRequest request) {
        var result = service.insertReport(request);
        return ResponseEntity.status(201).body(result);
    }}