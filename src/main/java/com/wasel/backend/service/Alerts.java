package com.wasel.backend.service;

import com.wasel.backend.model.Incident;

public interface Alerts {
    void createAlertsForIncident(Incident incident);
}