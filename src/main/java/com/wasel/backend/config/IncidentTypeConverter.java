package com.wasel.backend.config;

import com.wasel.backend.model.IncidentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IncidentTypeConverter implements AttributeConverter<IncidentType, String> {

    @Override
    public String convertToDatabaseColumn(IncidentType type) {
        return type != null ? type.getValue() : null;
    }

    @Override
    public IncidentType convertToEntityAttribute(String dbData) {
        return dbData != null ? IncidentType.fromValue(dbData) : null;
    }
}