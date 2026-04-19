package com.wasel.backend.dto;

public class InsertCheckpointRequest {

    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private int createdById;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public int getCreatedById() {
        return createdById;
    }
}