package com.wasel.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String API_KEY = "3b49dfc326c6f978b81e8b54210087fd";

    // 🌟 Constructor injection للـ RestTemplate
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public String getWeather(double lat, double lon) {

        String url = "https://api.openweathermap.org/data/2.5/weather?lat="
                + lat + "&lon=" + lon + "&appid=" + API_KEY;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("weather")) {
                return "Unknown";
            }

            List<Map<String, Object>> weatherList =
                    (List<Map<String, Object>>) response.get("weather");

            if (weatherList.isEmpty()) {
                return "Unknown";
            }

            return (String) weatherList.get(0).get("main");

        }
        catch (Exception e) {
            // ⚠️ يعالج التايم اوت وأي خطأ آخر
            return "Weather service unavailable (timeout or error)";
        }
    }

    public double applyWeatherImpact(
            String weatherStart,
            String weatherEnd,
            double duration,
            List<String> factors) {

        if ("Rain".equalsIgnoreCase(weatherStart) || "Rain".equalsIgnoreCase(weatherEnd)) {
            duration += 5;
            factors.add("Rain affecting route");
        } else if ("Fog".equalsIgnoreCase(weatherStart) || "Fog".equalsIgnoreCase(weatherEnd)) {
            duration += 7;
            factors.add("Fog reducing visibility");
        } else if ("Clouds".equalsIgnoreCase(weatherStart) || "Clouds".equalsIgnoreCase(weatherEnd)) {
            duration += 2;
            factors.add("Cloudy weather");
        } else if ("Clear".equalsIgnoreCase(weatherStart) && "Clear".equalsIgnoreCase(weatherEnd)) {
            factors.add("Clear weather conditions");
        } else {
            factors.add("Weather condition unknown");
        }

        return duration;
    }
}