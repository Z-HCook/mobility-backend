package com.wasel.backend.dto;

import com.wasel.backend.model.Checkpoint;
import com.wasel.backend.model.Incident;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class CheckpointHistoryResponse {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "checkpoint_id", nullable = false)
        private Checkpoint checkpoint;

        @ManyToOne
        @JoinColumn(name = "incident_id", nullable = false)
        private Incident incident;

        @Column(name = "ins_at")
        private LocalDateTime insAt;



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

