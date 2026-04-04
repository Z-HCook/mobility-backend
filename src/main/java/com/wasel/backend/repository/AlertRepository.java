package com.wasel.backend.repository;

import com.wasel.backend.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    // جلب تنبيهات مستخدم معين (للـ Mobile App لاحقاً)
    List<Alert> findByUserIdOrderByCreatedAtDesc(int userId);
}