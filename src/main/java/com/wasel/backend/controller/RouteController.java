package com.wasel.backend.controller;

import com.wasel.backend.dto.RouteRequest;
import com.wasel.backend.model.RouteResponse;
import com.wasel.backend.service.RouteEstimationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteEstimationService routeestimationservice;

    public RouteController(RouteEstimationService routeService) {
        this.routeestimationservice = routeService;
    }

    @PostMapping("/estimate")
    public RouteResponse estimate(@RequestBody RouteRequest request) {
        return routeestimationservice.estimateRoute(request);
    }
}
