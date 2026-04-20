package com.wasel.backend.repository;

import com.wasel.backend.model.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    // ✅ Pagination + Sort بدل findAll
    Page<Incident> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // ✅ Filter by status مع pagination
    Page<Incident> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
}