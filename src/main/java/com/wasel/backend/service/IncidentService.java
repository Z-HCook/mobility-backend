package com.wasel.backend.service;

import com.wasel.backend.model.Incident;
import com.wasel.backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final AlertService alertService;

    public IncidentService(IncidentRepository incidentRepository, AlertService alertService) {
        this.incidentRepository = incidentRepository;
        this.alertService = alertService;
    }

    @Transactional
    public Incident createAndNotify(Incident incident) {
        // 1. إعداد التوقيت والحالة الافتراضية إذا لم توجد
        if (incident.getCreatedAt() == null) {
            incident.setCreatedAt(LocalDateTime.now());
        }

        // 2. حفظ الحادث في قاعدة البيانات
        Incident savedIncident = incidentRepository.save(incident);

        // 3. إذا كان الحادث موثق (VERIFIED)، نشغل نظام التنبيهات فوراً
        if ("VERIFIED".equalsIgnoreCase(savedIncident.getStatus())) {
            alertService.createAlertsForIncident(savedIncident);
        }

        return savedIncident;
    }
}