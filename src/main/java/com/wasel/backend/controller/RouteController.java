package com.wasel.backend.controller;

import com.wasel.backend.dto.RouteRequest;
import com.wasel.backend.dto.RouteResponse;
import com.wasel.backend.usecase.EstimateRouteUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final EstimateRouteUseCase estimateRouteUseCase;

    public RouteController(EstimateRouteUseCase estimateRouteUseCase) {
        this.estimateRouteUseCase = estimateRouteUseCase;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> estimate(@RequestBody RouteRequest request) {
        RouteResponse response = estimateRouteUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}
