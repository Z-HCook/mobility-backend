package com.wasel.backend.repository;

import com.wasel.backend.model.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscriptions, Integer> {
    List<Subscriptions> findByIncidentType(String incidentType);
    List<Subscriptions> findByUserId(Integer userId);
}