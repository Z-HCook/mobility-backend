package com.wasel.backend.service;

import com.wasel.backend.model.Incident;
import com.wasel.backend.model.Report;
import com.wasel.backend.model.User;

public interface IncidentServiceint {
    Incident create(Report report, User moderator);
}

