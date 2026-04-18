package com.wasel.backend.controller;

import com.wasel.backend.dto.RouteRequest;
import com.wasel.backend.model.RouteResponse;
import com.wasel.backend.service.RateLimitingService;
import com.wasel.backend.usecase.EstimateRouteUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;




@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final EstimateRouteUseCase estimateRouteUseCase;
    private final RateLimitingService rateLimitingService;


    public RouteController(EstimateRouteUseCase estimateRouteUseCase, RateLimitingService rateLimitingService1) {
        this.estimateRouteUseCase = estimateRouteUseCase;

        this.rateLimitingService = rateLimitingService1;
    }

    @PostMapping
    public ResponseEntity<?> estimate(@RequestBody RouteRequest request, HttpServletRequest srequest) {
        Bucket bucket = rateLimitingService.resolveBucket(srequest.getRemoteAddr());
        if (bucket.tryConsume(1)) {
            RouteResponse response = estimateRouteUseCase.execute(request);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Slow down! You've reached the limit of requests per minute.");
        }
    }
}
