package com.wasel.backend.service;

import com.wasel.backend.dto.RouteRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RoutingService {

    private final RestTemplate restTemplate;

    public RoutingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getRoute(double startLat, double startLng,
                                        double endLat, double endLng) {

        String url = "http://router.project-osrm.org/route/v1/driving/"
                + startLng + "," + startLat + ";"
                + endLng + "," + endLat
                + "?overview=false";

        return requestRoute(url);
    }

    public Map<String, Object> getRouteWithWaypoints(double startLat, double startLng,
                                                     double endLat, double endLng,
                                                     List<double[]> waypoints) {

        StringBuilder sb = new StringBuilder();
        sb.append(startLng).append(",").append(startLat);

        if (waypoints != null) {
            for (double[] wp : waypoints) {
                sb.append(";").append(wp[1]).append(",").append(wp[0]);
            }
        }

        sb.append(";").append(endLng).append(",").append(endLat);

        String url = "http://router.project-osrm.org/route/v1/driving/"
                + sb.toString()
                + "?overview=false";

        return requestRoute(url);
    }

    public Map<String, Object> getRouteWithAvoid(RouteRequest request, List<String> factors) {

        if (request.getStartLat() == 0.0 && request.getStartLng() == 0.0) {
            throw new IllegalArgumentException("Start coordinates are invalid!");
        }

        if (request.getEndLat() == 0.0 && request.getEndLng() == 0.0) {
            throw new IllegalArgumentException("End coordinates are invalid!");
        }

        List<double[]> avoidPoints = new ArrayList<>();

        if (request.getAvoidAreas() == null) {
            request.setAvoidAreas(new ArrayList<>());
        }

        for (RouteRequest.AvoidArea cp : request.getAvoidAreas()) {
            factors.add(cp.getName() + " you have chosen");

            double radius = cp.getRadius() > 0 ? cp.getRadius() : 0.5;

            List<double[]> points =
                    generateCirclePoints(cp.getLat(), cp.getLng(), radius, 8);

            points.removeIf(p -> p[0] == 0.0 && p[1] == 0.0);

            avoidPoints.addAll(points);
        }

        return getRouteWithWaypoints(
                request.getStartLat(),
                request.getStartLng(),
                request.getEndLat(),
                request.getEndLng(),
                avoidPoints
        );
    }

    private Map<String, Object> requestRoute(String url) {
        try {
            Map<String, Object> response =
                    restTemplate.getForObject(url, Map.class);

            if (response == null) {
                throw new IllegalStateException("Empty routing response");
            }

            return response;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Routing service unavailable: " + e.getMessage()
            );
        }
    }

    public double extractDistance(Map<String, Object> response) {
        try {
            List routes = (List) response.get("routes");
            Map route = (Map) routes.get(0);

            return (double) route.get("distance") / 1000;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract route data");
        }
    }

    public double extractDuration(Map<String, Object> response) {
        try {
            List routes = (List) response.get("routes");
            Map route = (Map) routes.get(0);

            return (double) route.get("duration") / 60;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract route data");
        }
    }

    public static List<double[]> generateCirclePoints(double lat, double lng,
                                                      double radiusKm, int pointsCount) {

        List<double[]> points = new ArrayList<>();
        double earthRadius = 6371;

        for (int i = 0; i < pointsCount; i++) {

            double angle = 2 * Math.PI * i / pointsCount;

            double dLat = (radiusKm / earthRadius) * Math.sin(angle);
            double dLng = (radiusKm / (earthRadius * Math.cos(Math.toRadians(lat))))
                    * Math.cos(angle);

            double newLat = lat + Math.toDegrees(dLat);
            double newLng = lng + Math.toDegrees(dLng);

            points.add(new double[]{newLat, newLng});
        }

        return points;
    }
}