package com.wasel.backend.model;

import java.util.List;

public class RouteResponse {
    public double distance;
    public double duration;
    public List<String> factors;

    public RouteResponse(double distance, double duration, List<String> factors) {
        this.distance = distance;
        this.duration = duration;
        this.factors = factors;
    }
}
