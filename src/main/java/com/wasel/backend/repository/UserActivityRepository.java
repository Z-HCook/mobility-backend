package com.wasel.backend.repository;

import com.wasel.backend.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityRepository
        extends JpaRepository<UserActivity, Integer> {

    int countByUserId(Integer userId);
}