package com.wasel.backend.usecase;

import com.wasel.backend.dto.RouteRequest;
import com.wasel.backend.dto.RouteResponse;
import com.wasel.backend.service.RouteEstimationService;
import org.springframework.stereotype.Component;

@Component
public class EstimateRouteUseCase {

    private final RouteEstimationService routeEstimationService;

    public EstimateRouteUseCase(RouteEstimationService routeEstimationService) {
        this.routeEstimationService = routeEstimationService;
    }

    public RouteResponse execute(RouteRequest request) {
        return routeEstimationService.estimateRoute(request);
    }
}