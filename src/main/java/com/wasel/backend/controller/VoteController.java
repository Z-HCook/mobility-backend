package com.wasel.backend.controller;

import com.wasel.backend.dto.VoteRequest;
import com.wasel.backend.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService service;

    public VoteController(VoteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> vote(@RequestBody VoteRequest request) {
        var result = service.vote(request);
        return ResponseEntity.status(201).body(result);
    }}
