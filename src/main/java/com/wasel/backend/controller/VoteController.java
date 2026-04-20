 package com.wasel.backend.controller;

import com.wasel.backend.dto.VoteRequest;
import com.wasel.backend.service.VoteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/votes")
public class VoteController {

    private final VoteService service;

    public VoteController(VoteService service) {
        this.service = service;
    }

    @PostMapping
    public String vote(@RequestBody VoteRequest request) {
        return service.vote(request);
    }
}