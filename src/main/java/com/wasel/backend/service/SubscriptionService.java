package com.wasel.backend.service;

import com.wasel.backend.dto.SubscriptionRequest;
import com.wasel.backend.model.Subscriptions;
import com.wasel.backend.repository.SubscriptionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    // ✅ نمسح كاش المشتركين لأن هناك مشترك جديد انضم لنوع معين من الحوادث
    @CacheEvict(value = "subscribers", allEntries = true)
    public Subscriptions createSubscription(SubscriptionRequest request) {
        Subscriptions subscription = new Subscriptions();

        subscription.setUserId(request.getUserId());
        subscription.setRegion(request.getRegion());
        subscription.setLatitude(request.getLatitude());
        subscription.setLongitude(request.getLongitude());

        // منطق القيمة الافتراضية للقطر الجغرافي
        subscription.setRadiusKm(request.getRadiusKm() != null ? request.getRadiusKm() : 5.0);

        subscription.setIncidentType(request.getIncidentType());
        subscription.setCreatedAt(OffsetDateTime.now());

        return subscriptionRepository.save(subscription);
    }

    // ✅ تخزين اشتراكات المستخدم في الكاش لتسريع عرضها في صفحته الشخصية
    @Cacheable(value = "userSubscriptions", key = "#userId")
    public List<Subscriptions> getSubscriptionsByUserId(int userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Transactional
    // ✅ نمسح كاش المشتركين وكاش المستخدم عند حذف اشتراك
    @CacheEvict(value = {"subscribers", "userSubscriptions"}, allEntries = true)
    public void deleteSubscription(int id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new RuntimeException("الاشتراك غير موجود!");
        }
        subscriptionRepository.deleteById(id);
    }
}