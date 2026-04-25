package com.wasel.backend.repository;

import com.wasel.backend.model.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {


    Page<Incident> findAllByOrderByCreatedAtDesc(Pageable pageable);


    Page<Incident> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    @Query("SELECT i FROM Incident i WHERE i.createdAt >= :time")
    List<Incident> findRecentIncidents(LocalDateTime time);

    Page<Incident> findByTypeOrderByCreatedAtDesc(String type, Pageable pageable);
}