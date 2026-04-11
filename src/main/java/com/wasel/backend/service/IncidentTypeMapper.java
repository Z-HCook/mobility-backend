package com.wasel.backend.service;

import org.springframework.stereotype.Component;

@Component
public class IncidentTypeMapper {

    public String map(String category) {
        return switch (category.toLowerCase()) {
            case "traffic" -> "DELAY";
            case "safety" -> "ACCIDENT";
            case "weather" -> "WEATHER";
            default -> "CLOSURE";
        };
    }
}