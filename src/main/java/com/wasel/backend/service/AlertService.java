package com.wasel.backend.service;

import com.wasel.backend.model.Alert;
import com.wasel.backend.model.Subscriptions;
import com.wasel.backend.model.Incident;
import com.wasel.backend.repository.AlertRepository;
import com.wasel.backend.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final SubscriptionRepository subscriptionRepository;

    public AlertService(AlertRepository alertRepository, SubscriptionRepository subscriptionRepository) {
        this.alertRepository = alertRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public void createAlertsForIncident(Incident incident) {
        // 1. جلب المشتركين حسب النوع فقط (بدون isActive)
        List<Subscriptions> subscribers = subscriptionRepository.findByIncidentType(incident.getType());

        if (subscribers != null) {
            for (Subscriptions sub : subscribers) {
                // 2. فحص المسافة الجغرافية (Haversine)
                double distance = calculateDistance(
                        incident.getLatitude(), incident.getLongitude(),
                        sub.getLatitude(), sub.getLongitude()
                );

                // القطر المسموح (إذا لم يحدده المستخدم نعتبره 5 كم افتراضياً)
                double radius = (sub.getRadiusKm() != null) ? sub.getRadiusKm() : 5.0;

                if (distance <= radius) {
                    saveAlert(incident, sub);
                }
            }
        }
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

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // نصف قطر الأرض بالكم
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}