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

    // خريطة لتخزين "السلال" (Buckets) لكل IP مستخدم بشكل آمن
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // ميثود لجلب السطل الخاص بالمستخدم أو إنشاء واحد جديد إذا كان أول مرة يطلب
    public Bucket resolveBucket(String clientIp) {
        return buckets.computeIfAbsent(clientIp, this::newBucket);
    }

    private Bucket newBucket(String clientIp) {
        // الحد: 10 طلبات في الدقيقة
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .build();
    }
}