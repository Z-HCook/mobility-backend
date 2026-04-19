package com.wasel.backend.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {


    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();


    public Bucket resolveBucket(String clientIp) {
        return buckets.computeIfAbsent(clientIp, this::newBucket);
    }

    private Bucket newBucket(String clientIp) {

        return Bucket.builder()
                .addLimit(Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofMinutes(1))))
                .build();
    }
}