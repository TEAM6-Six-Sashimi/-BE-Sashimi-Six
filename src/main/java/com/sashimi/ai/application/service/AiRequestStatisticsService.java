package com.sashimi.ai.application.service;

import com.sashimi.ai.domain.model.AiFeatureType;
import com.sashimi.ai.domain.model.AiUsagePeriod;
import com.sashimi.ai.infrastructure.persistence.AiRequestCountProjection;
import com.sashimi.ai.infrastructure.persistence.SpringDataAiRequestHistoryRepository;
import com.sashimi.ai.presentation.api.response.AiRequestStatisticsResponse;
import com.sashimi.ai.presentation.api.response.AiUsageDataResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                toCountMap(
                        repository.countHourlyRequestsByFeatureType(
                                from,
                                to
                        )
                );

        List<AiUsageDataResponse> data =
                java.util.stream.IntStream.rangeClosed(0, 23)
                        .mapToObj(hour -> toResponse(
                                hour + "시",
                                countMap.getOrDefault(
                                        hour,
                                        new EnumMap<>(AiFeatureType.class)
                                )
                        ))
                        .toList();

        return new AiRequestStatisticsResponse(
                AiUsagePeriod.hourly.name(),
                data
        );
    }

    private AiRequestStatisticsResponse getDailyUsage() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        Map<Integer, Map<AiFeatureType, Long>> countMap =
                toCountMap(
                        repository.countDailyRequestsByFeatureType(
                                from,
                                to
                        )
                );

        List<AiUsageDataResponse> data =
                java.util.stream.IntStream.rangeClosed(0, 6)
                        .mapToObj(dayOffset -> {
                            LocalDate date =
                                    startDate.plusDays(dayOffset);

                            return toResponse(
                                    toDayLabel(date),
                                    countMap.getOrDefault(
                                            toMysqlDayOfWeek(date),
                                            new EnumMap<>(AiFeatureType.class)
                                    )
                            );
                        })
                        .toList();

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
                counts.getOrDefault(
                        AiFeatureType.JOB_POSTING_ANALYSIS,
                        0L
                ),
                counts.getOrDefault(
                        AiFeatureType.RESUME_REVIEW,
                        0L
                ),
                counts.getOrDefault(
                        AiFeatureType.COVER_LETTER_REVIEW,
                        0L
                )
        );
    }

    private String toDayLabel(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }

    private int toMysqlDayOfWeek(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SUNDAY -> 1;
            case MONDAY -> 2;
            case TUESDAY -> 3;
            case WEDNESDAY -> 4;
            case THURSDAY -> 5;
            case FRIDAY -> 6;
            case SATURDAY -> 7;
        };
    }
}