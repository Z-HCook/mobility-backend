package com.wasel.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double latitude;
    private Double longitude;

    private String status;


    @Column(name = "credibility_score")
    private Float credibilityScore;

    @Column(name = "duplicate_of")
    private Integer duplicateOf;

    @Column(name = "linked_incident_id")
    private Integer linkedIncidentId;

    @Column(name = "linked_checkpoint_id")
    private Integer linkedCheckpointId;

    @Column(name = "is_promoted")
    private Boolean isPromoted;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String type;

    @Column(name = "duplicate_count")
    private Integer duplicateCount;
    // Constructors
    public Report() {}

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Float getCredibilityScore() { return credibilityScore; }
    public void setCredibilityScore(Float credibilityScore) { this.credibilityScore = credibilityScore; }

    public Integer getDuplicateOf() { return duplicateOf; }
    public void setDuplicateOf(Integer duplicateOf) { this.duplicateOf = duplicateOf; }

    public Integer getLinkedIncidentId() { return linkedIncidentId; }
    public void setLinkedIncidentId(Integer linkedIncidentId) { this.linkedIncidentId = linkedIncidentId; }

    public Integer getLinkedCheckpointId() { return linkedCheckpointId; }
    public void setLinkedCheckpointId(Integer linkedCheckpointId) { this.linkedCheckpointId = linkedCheckpointId; }

    public Boolean getIsPromoted() { return isPromoted; }
    public void setIsPromoted(Boolean isPromoted) { this.isPromoted = isPromoted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getDuplicateCount() {
        return duplicateCount;
    }

    public void setDuplicateCount(Integer duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

}