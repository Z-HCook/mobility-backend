package com.wasel.backend.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.wasel.backend.model.IncidentStatus;

@Converter(autoApply = true)
public class IncidentStatusConverter implements AttributeConverter<IncidentStatus, String> {

    @Override
    public String convertToDatabaseColumn(IncidentStatus status) {
        return status != null ? status.getValue() : null;
    }

    @Override
    public IncidentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return IncidentStatus.fromValue(dbData);
    }
}