package com.wasel.backend.service;

import com.wasel.backend.dto.SubscriptionRequest;
import com.wasel.backend.exception.ResourceNotFoundException;
import com.wasel.backend.model.Subscriptions;
import com.wasel.backend.repository.SubscriptionRepository;
import com.wasel.backend.repository.UserRepository; // 1. استيراد الريبوزتوري الخاص بالمستخدمين
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository; // 2. تعريف الريبوزتوري

    // 3. تحديث الـ Constructor ليحتوي على الـ UserRepository
    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @CacheEvict(value = "subscribers", allEntries = true)
    public Subscriptions createSubscription(SubscriptionRequest request) {
        // (إضافي) يفضل أيضاً التحقق من وجود المستخدم عند إنشاء اشتراك
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("User not found");
        }

        Subscriptions subscription = new Subscriptions();
        subscription.setUserId(request.getUserId());
        subscription.setRegion(request.getRegion());
        subscription.setLatitude(request.getLatitude());
        subscription.setLongitude(request.getLongitude());
        subscription.setRadiusKm(request.getRadiusKm() != null ? request.getRadiusKm() : 5.0);
        subscription.setIncidentType(request.getIncidentType());
        subscription.setCreatedAt(OffsetDateTime.now());

        return subscriptionRepository.save(subscription);
    }

    public List<Subscriptions> getSubscriptionsByUserId(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with ID " + userId + " was not found.");
        }
        return subscriptionRepository.findByUserId(userId);
    }

    @Transactional
    @CacheEvict(value = {"subscribers", "userSubscriptions"}, allEntries = true)
    public void deleteSubscription(int id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new IllegalStateException("Subscription not found");
        }
        subscriptionRepository.deleteById(id);
    }
}