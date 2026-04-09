package com.wasel.backend.service;

import com.wasel.backend.dto.RouteRequest;
import com.wasel.backend.model.Incident;
import com.wasel.backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }
    public int countIncidentsNearRouteEndpoints(RouteRequest request) {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        List<Incident> incidents = incidentRepository.findRecentIncidents(thirtyMinutesAgo);

        Set<Incident> unique = new HashSet<>();

        for (var i : incidents) {

            double diststart = distance(request.getStartLat(), request.getStartLng(),
                    i.getLatitude(), i.getLongitude());
            double distend = distance(request.getEndLat(), request.getEndLng(),
                    i.getLatitude(), i.getLongitude());

            if (diststart < 3 || distend < 3) {
                unique.add(i);
            }
        }

        return unique.size();



    }


    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
