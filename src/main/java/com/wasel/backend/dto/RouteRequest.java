package com.wasel.backend.dto;

import java.util.List;

public class RouteRequest {
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;

    public List<AvoidArea> avoidAreas; // كل دائرة تحتوي على اسم، خط عرض، خط طول، نصف قطر
    public static class AvoidArea {
        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        private String name;
        private double lat;

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        private double lng;
        private double radius; // km
    }
    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public List<AvoidArea> getAvoidAreas() {
        return avoidAreas;
    }

    public void setAvoidAreas(List<AvoidArea> avoidAreas) {
        this.avoidAreas = avoidAreas;
    }

    public double getEndLng() {
        return endLng;
    }

    public void setEndLng(double endLng) {
        this.endLng = endLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }


}