package com.wasel.backend.controller;
import com.wasel.backend.dto.RegisterRequest;
import com.wasel.backend.service.InsertService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/insert")
public class InsertController {


    private final InsertService insservice;

    public InsertController(InsertService insertservice) {
        this.insservice = insertservice;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return insservice.register(request);
    }
}
