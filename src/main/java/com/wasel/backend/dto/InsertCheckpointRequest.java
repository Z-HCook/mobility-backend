package com.wasel.backend.dto;

public class InsertCheckpointRequest {
    public String name;
    public double latitude;
    public double longitude;
    public String description;
    public int createdById; // ID للـ User اللي عامل الإدخال
}