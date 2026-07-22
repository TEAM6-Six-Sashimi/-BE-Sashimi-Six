package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.domain.model.AiRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataAiRequestHistoryRepository
        extends JpaRepository<AiRequestHistoryJpaEntity, Long> {

    @Query("""
            select hour(h.createdAt) as bucket,
                   h.featureType as featureType,
                   count(h) as requestCount
            from AiRequestHistoryJpaEntity h
            where h.createdAt >= :from
              and h.createdAt < :to
            group by hour(h.createdAt), h.featureType
            """)
    List<AiRequestCountProjection> countHourlyRequestsByFeatureType(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            select function('dayofweek', h.createdAt) as bucket,
                   h.featureType as featureType,
                   count(h) as requestCount
            from AiRequestHistoryJpaEntity h
            where h.createdAt >= :from
              and h.createdAt < :to
            group by function('dayofweek', h.createdAt), h.featureType
            """)
    List<AiRequestCountProjection> countDailyRequestsByFeatureType(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query(value = """
        select *
        from ai_request_histories
        where user_id = :userId
          and feature_type = 'RESUME_REVIEW'
          and status = 'COMPLETED'
          and json_unquote(json_extract(request_snapshot_json, '$')) = :resumeId
          and result_json is not null
        order by created_at desc
        limit 1
        """, nativeQuery = true)

    Optional<AiRequestHistoryJpaEntity> findLatestCompletedResumeReview(
            @Param("userId") Long userId,
            @Param("resumeId") String resumeId
    );

    long countByUserIdAndFeatureTypeAndCreatedAtGreaterThanEqual(
            Long userId,
            AiFeatureType featureType,
            LocalDateTime from
    );

    long countByUserIdAndCreatedAtGreaterThanEqual(
            Long userId,
            LocalDateTime from
    );

    Optional<AiRequestHistoryJpaEntity> findFirstByUserIdAndFeatureTypeAndStatusOrderByCreatedAtDesc(
            Long userId,
            AiFeatureType featureType,
            AiRequestStatus status
    );

    Optional<AiRequestHistoryJpaEntity> findByIdAndUserIdAndFeatureType(
            Long id,
            Long userId,
            AiFeatureType featureType
    );

    Optional<AiRequestHistoryJpaEntity> findFirstByUserIdAndFeatureTypeAndStatusAndRequestSnapshotJsonOrderByCreatedAtDesc(
            Long userId,
            AiFeatureType featureType,
            AiRequestStatus status,
            String requestSnapshotJson
    );

    long deleteByCreatedAtBefore(
            LocalDateTime threshold
    );

    Optional<AiRequestHistoryJpaEntity> findByIdAndUserIdAndFeatureTypeAndStatus(
            Long id,
            Long userId,
            AiFeatureType featureType,
            AiRequestStatus status
    );
}