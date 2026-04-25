package com.wasel.backend.service;

import com.wasel.backend.model.Alert;
import com.wasel.backend.model.Subscriptions;
import com.wasel.backend.model.Incident;
import com.wasel.backend.repository.AlertRepository;
import com.wasel.backend.repository.SubscriptionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService implements Alerts{

    private final AlertRepository alertRepository;
    private final SubscriptionRepository subscriptionRepository;

    public AlertService(AlertRepository alertRepository, SubscriptionRepository subscriptionRepository) {
        this.alertRepository = alertRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    @Transactional
    public void createAlertsForIncident(Incident incident) {
        String normalizedType = normalizeType(incident.getType());

        List<Subscriptions> subscribers = subscriptionRepository.findByIncidentType(incident.getType());

        if (subscribers != null) {
            for (Subscriptions sub : subscribers) {
                double distance = incident.distance(
                        incident.getLatitude(), incident.getLongitude(),
                        sub.getLatitude(), sub.getLongitude()
                );

                double radius = (sub.getRadiusKm() != null) ? sub.getRadiusKm() : 5.0;

                if (distance <= radius) {
                    saveAlert(incident, sub);
                }
            }
        }
    }

    @CacheEvict(value = "subscribers", key = "#incidentType")
    public List<Subscriptions> getSubscribersByType(String incidentType) {
        return subscriptionRepository.findByIncidentType(incidentType);
    }

    private void saveAlert(Incident incident, Subscriptions sub) {
        Alert alert = new Alert();
        alert.setUserId(sub.getUserId());
        alert.setIncidentId(incident.getId());
        alert.setMessage("تنبيه واصل: حدث " + incident.getType() + " بالقرب من موقعك المتابع.");
        alert.setIsRead(false);
        alert.setCreatedAt(LocalDateTime.now());
        alertRepository.save(alert);
    }

    private String normalizeType(String type) {
        if (type == null) return null;

        type = type.toLowerCase();

        switch (type) {
            case "closure":
                return "CLOSURE";
            case "accident":
                return "ACCIDENT";
            case "weather":
                return "WEATHER";
            case "delay":
                return "TRAFFIC";
            default:
                return type.toUpperCase();
        }
    }
}