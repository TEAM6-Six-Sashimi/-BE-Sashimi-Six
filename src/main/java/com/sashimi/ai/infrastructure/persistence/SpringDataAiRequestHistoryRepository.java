package com.sashimi.ai.infrastructure.persistence;

import com.sashimi.ai.domain.model.AiFeatureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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

    long countByUserIdAndFeatureTypeAndCreatedAtGreaterThanEqual(
            Long userId,
            AiFeatureType featureType,
            LocalDateTime from
    );

    long countByUserIdAndCreatedAtGreaterThanEqual(
            Long userId,
            LocalDateTime from
    );
}