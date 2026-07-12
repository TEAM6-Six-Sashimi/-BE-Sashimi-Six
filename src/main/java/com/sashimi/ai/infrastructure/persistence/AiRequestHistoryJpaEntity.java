package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.domain.model.AiRequestStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_request_histories")
public class AiRequestHistoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_request_history_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feature_type", nullable = false, length = 50)
    private AiFeatureType featureType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AiRequestStatus status;

    @Column(name = "request_snapshot_json", columnDefinition = "json")
    private String requestSnapshotJson;

    @Column(name = "result_json", columnDefinition = "json")
    private String resultJson;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected AiRequestHistoryJpaEntity() {
    }

    public static AiRequestHistoryJpaEntity started(
            Long userId,
            AiFeatureType featureType,
            String requestSnapshotJson
    ) {
        AiRequestHistoryJpaEntity entity = new AiRequestHistoryJpaEntity();
        entity.userId = userId;
        entity.featureType = featureType;
        entity.status = AiRequestStatus.PENDING;
        entity.requestSnapshotJson = requestSnapshotJson;
        entity.createdAt = LocalDateTime.now();
        return entity;
    }

    public void complete(String resultJson) {
        this.status = AiRequestStatus.COMPLETED;
        this.resultJson = resultJson;
        this.completedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = AiRequestStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AiFeatureType getFeatureType() {
        return featureType;
    }
}