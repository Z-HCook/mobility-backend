package com.wasel.backend.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IncidentType {
    DELAY("delay"),
    WEATHER("weather"),
    ACCIDENT("accident"),
    CLOSURE("closure");

    private final String value;

    IncidentType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    public static IncidentType fromValue(String value) {
        for (IncidentType t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Invalid IncidentType: " + value);
    }
}