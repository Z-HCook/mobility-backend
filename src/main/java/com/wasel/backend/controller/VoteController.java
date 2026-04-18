package com.wasel.backend.controller;

import com.wasel.backend.dto.VoteRequest;
import com.wasel.backend.usecase.VoteUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteUseCase voteUseCase;

    public VoteController(VoteUseCase voteUseCase) {
        this.voteUseCase = voteUseCase;
    }

    @PostMapping
    public ResponseEntity<String> vote(@RequestBody VoteRequest request) {
        return ResponseEntity.ok(voteUseCase.execute(request));
    }
}