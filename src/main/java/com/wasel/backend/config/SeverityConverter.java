package com.wasel.backend.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.wasel.backend.model.Severity;

@Converter(autoApply = true)
public class SeverityConverter implements AttributeConverter<Severity, String> {

    @Override
    public String convertToDatabaseColumn(Severity severity) {
        return severity != null ? severity.getValue() : null;
    }

    @Override
    public Severity convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Severity.fromValue(dbData);
    }
}