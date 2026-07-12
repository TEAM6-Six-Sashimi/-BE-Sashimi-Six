package com.sashimi.ai.application.service;

import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.domain.model.AiUsagePeriod;
import com.sashimi.ai.infrastructure.persistence.AiRequestCountProjection;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.ai.presentation.api.response.AiRequestStatisticsResponse;
import com.sashimi.ai.presentation.api.response.AiUsageDataResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AiRequestStatisticsService {

    private final SpringDataAiRequestHistoryRepository repository;

    public AiRequestStatisticsService(
            SpringDataAiRequestHistoryRepository repository
    ) {
        this.repository = repository;
    }

    public AiRequestStatisticsResponse getAiUsage(
            AiUsagePeriod period
    ) {
        if (period == AiUsagePeriod.daily) {
            return getDailyUsage();
        }

        return getHourlyUsage();
    }

    private AiRequestStatisticsResponse getHourlyUsage() {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        Map<Integer, Map<AiFeatureType, Long>> countMap =
                toCountMap(repository.countHourlyRequestsByFeatureType(from, to));

        List<AiUsageDataResponse> data =
                java.util.stream.IntStream.rangeClosed(0, 23)
                        .mapToObj(hour -> toResponse(
                                hour + "시",
                                countMap.getOrDefault(hour, new EnumMap<>(AiFeatureType.class))
                        ))
                        .toList();

        return new AiRequestStatisticsResponse(
                AiUsagePeriod.hourly.name(),
                data
        );
    }

    private AiRequestStatisticsResponse getDailyUsage() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDateTime from = monday.atStartOfDay();
        LocalDateTime to = monday.plusDays(7).atStartOfDay();

        Map<Integer, Map<AiFeatureType, Long>> countMap =
                toCountMap(repository.countDailyRequestsByFeatureType(from, to));

        List<AiUsageDataResponse> data = List.of(
                toResponse("월", countMap.getOrDefault(2, new EnumMap<>(AiFeatureType.class))),
                toResponse("화", countMap.getOrDefault(3, new EnumMap<>(AiFeatureType.class))),
                toResponse("수", countMap.getOrDefault(4, new EnumMap<>(AiFeatureType.class))),
                toResponse("목", countMap.getOrDefault(5, new EnumMap<>(AiFeatureType.class))),
                toResponse("금", countMap.getOrDefault(6, new EnumMap<>(AiFeatureType.class))),
                toResponse("토", countMap.getOrDefault(7, new EnumMap<>(AiFeatureType.class))),
                toResponse("일", countMap.getOrDefault(1, new EnumMap<>(AiFeatureType.class)))
        );

        return new AiRequestStatisticsResponse(
                AiUsagePeriod.daily.name(),
                data
        );
    }

    private Map<Integer, Map<AiFeatureType, Long>> toCountMap(
            List<AiRequestCountProjection> projections
    ) {
        Map<Integer, Map<AiFeatureType, Long>> countMap =
                new java.util.HashMap<>();

        for (AiRequestCountProjection projection : projections) {
            countMap
                    .computeIfAbsent(
                            projection.getBucket(),
                            key -> new EnumMap<>(AiFeatureType.class)
                    )
                    .put(
                            projection.getFeatureType(),
                            projection.getRequestCount()
                    );
        }

        return countMap;
    }

    private AiUsageDataResponse toResponse(
            String label,
            Map<AiFeatureType, Long> counts
    ) {
        return new AiUsageDataResponse(
                label,
                counts.getOrDefault(AiFeatureType.JOB_POSTING_ANALYSIS, 0L),
                counts.getOrDefault(AiFeatureType.RESUME_REVIEW, 0L),
                counts.getOrDefault(AiFeatureType.COVER_LETTER_REVIEW, 0L)
        );
    }
}