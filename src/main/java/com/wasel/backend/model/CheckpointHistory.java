package com.wasel.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoint_history")
public class CheckpointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // علاقة مع Checkpoint
    @ManyToOne
    @JoinColumn(name = "checkpoint_id", nullable = false)
    private Checkpoint checkpoint;

    // علاقة مع Incident
    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(name = "ins_at")
    private LocalDateTime insAt;

    // Constructors
    public CheckpointHistory() {}

    public CheckpointHistory(Checkpoint checkpoint, Incident incident, LocalDateTime insAt) {
        this.checkpoint = checkpoint;
        this.incident = incident;
        this.insAt = insAt;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public LocalDateTime getInsAt() {
        return insAt;
    }

    public void setInsAt(LocalDateTime insAt) {
        this.insAt = insAt;
    }
}