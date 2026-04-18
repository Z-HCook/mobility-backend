package com.wasel.backend.usecase;

import com.wasel.backend.dto.SubscriptionRequest;
import com.wasel.backend.model.Subscriptions;
import com.wasel.backend.service.SubscriptionService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionUseCase {

    private final SubscriptionService subscriptionService;

    public SubscriptionUseCase(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public Subscriptions create(SubscriptionRequest request) {
        return subscriptionService.createSubscription(request);
    }

    public List<Subscriptions> getByUserId(int userId) {
        return subscriptionService.getSubscriptionsByUserId(userId);
    }

    public void delete(int id) {
        subscriptionService.deleteSubscription(id);
    }
}