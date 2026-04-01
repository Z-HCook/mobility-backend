package com.wasel.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IncidentStatus {

    PENDING("pending"),
    VERIFIED("verified"),
    CLOSED("closed");

    private final String value;

    IncidentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // 🔥 مهم جداً عشان Postman يشتغل
    @JsonCreator
    public static IncidentStatus fromValue(String value) {
        for (IncidentStatus status : IncidentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid IncidentStatus: " + value);
    }



}