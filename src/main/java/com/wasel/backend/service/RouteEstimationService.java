package com.wasel.backend.service;

import com.wasel.backend.dto.RouteRequest;
import com.wasel.backend.model.RouteResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RouteEstimationService {


    private final WeatherService weatherService;
    private final IncidentService incidentService;
    private final RoutingService routingService;

    public RouteEstimationService(
            WeatherService weatherService,
            IncidentService incidentService,
            RoutingService routingService) {


        this.weatherService = weatherService;
        this.incidentService = incidentService;
        this.routingService = routingService;
    }

    public RouteResponse estimateRoute(RouteRequest request) {

        List<String> factors = new ArrayList<>();
        Map<String, Object> response = routingService.getRouteWithAvoid(request , factors);

        double distance = routingService.extractDistance(response);
        double duration = routingService.extractDuration(response);


        duration = applyIncidentImpact(request, duration, factors);

        String weatherStart = weatherService.getWeather(request.getStartLat(), request.getStartLng());
        String weatherEnd = weatherService.getWeather(request.getEndLat(), request.getEndLng());
        duration = weatherService.applyWeatherImpact(weatherStart, weatherEnd, duration, factors);

        return new RouteResponse(distance, duration, factors);
    }






    private double applyIncidentImpact(RouteRequest request, double duration, List<String> factors) {
        int incidents = incidentService.countIncidentsNearRouteEndpoints(request);
        if (incidents > 0) {
            double impact = Math.min(incidents * 0.05, 0.3); // max 30%
            duration += duration * impact;
            factors.add(incidents + " traffic incidents affecting route");
        }
        return duration;
    }
}