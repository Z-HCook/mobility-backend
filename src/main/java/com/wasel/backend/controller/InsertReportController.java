 package com.wasel.backend.controller;

import com.wasel.backend.dto.InsertReportRequest;
import com.wasel.backend.usecase.InsertReportUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insert")
public class InsertReportController {

    private final InsertReportUseCase insertReportUseCase;

    public InsertReportController(InsertReportUseCase insertReportUseCase) {
        this.insertReportUseCase = insertReportUseCase;
    }

    @PostMapping("/report")
    public String insertReport(@RequestBody InsertReportRequest request) {
        return insertReportUseCase.execute(request);
    }
}