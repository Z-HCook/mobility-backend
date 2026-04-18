package com.wasel.backend.usecase;

import com.wasel.backend.dto.VoteRequest;
import com.wasel.backend.service.VoteService;
import org.springframework.stereotype.Component;

@Component
public class VoteUseCase {

    private final VoteService voteService;

    public VoteUseCase(VoteService voteService) {
        this.voteService = voteService;
    }

    public String execute(VoteRequest request) {
        return voteService.vote(request);
    }
}