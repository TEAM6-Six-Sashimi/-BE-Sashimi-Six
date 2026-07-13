package com.sashimi.stats.login.application.service;

import com.sashimi.stats.login.application.port.PrometheusQueryPort;
import com.sashimi.stats.login.application.usecase.AdminLoginStatsQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminLoginStatsQueryService implements AdminLoginStatsQueryUseCase {

    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");
    private static final String LOGIN_SUCCESS_PROM_QUERY =
            "increase(auth_login_success_total{job=\"sashimi\"}[%s])";
    private static final String[] WEEKDAY_LABELS = {"월", "화", "수", "목", "금", "토", "일"};

    private final PrometheusQueryPort prometheusQueryPort;

    @Override
    public LoginStats getStats(LoginStatsPeriod period) {
        return switch (period) {
            case HOURLY -> getHourlyStats();
            case DAILY -> getDailyStats();
        };
    }

    private LoginStats getHourlyStats() {
        ZonedDateTime startOfToday = LocalDate.now(SERVICE_ZONE).atStartOfDay(SERVICE_ZONE);
        Instant queryStart = startOfToday.plusHours(1).toInstant();
        Instant queryEnd = startOfToday.plusDays(1).toInstant();

        Map<Long, Double> countsByEpochSecond = fetchCountsByEpochSecond(
                LOGIN_SUCCESS_PROM_QUERY.formatted("1h"),
                queryStart,
                queryEnd,
                3600
        );

        List<LoginStatsItem> data = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            Instant bucketEnd = startOfToday.plusHours(hour + 1L).toInstant();
            data.add(new LoginStatsItem(
                    "%02d시".formatted(hour),
                    resolveCount(countsByEpochSecond, bucketEnd)
            ));
        }

        return new LoginStats(LoginStatsPeriod.HOURLY, data);
    }

    private LoginStats getDailyStats() {
        LocalDate monday = LocalDate.now(SERVICE_ZONE).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime startOfMonday = monday.atStartOfDay(SERVICE_ZONE);
        Instant queryStart = startOfMonday.plusDays(1).toInstant();
        Instant queryEnd = startOfMonday.plusWeeks(1).toInstant();

        Map<Long, Double> countsByEpochSecond = fetchCountsByEpochSecond(
                LOGIN_SUCCESS_PROM_QUERY.formatted("1d"),
                queryStart,
                queryEnd,
                86400
        );

        List<LoginStatsItem> data = new ArrayList<>();
        for (int day = 0; day < 7; day++) {
            Instant bucketEnd = startOfMonday.plusDays(day + 1L).toInstant();
            data.add(new LoginStatsItem(
                    WEEKDAY_LABELS[day],
                    resolveCount(countsByEpochSecond, bucketEnd)
            ));
        }

        return new LoginStats(LoginStatsPeriod.DAILY, data);
    }

    private Map<Long, Double> fetchCountsByEpochSecond(
            String promQuery,
            Instant start,
            Instant end,
            long stepSeconds
    ) {
        List<PrometheusQueryPort.PrometheusPoint> points =
                prometheusQueryPort.queryRange(promQuery, start, end, stepSeconds);

        return points.stream()
                .collect(Collectors.toMap(
                        point -> point.timestamp().getEpochSecond(),
                        PrometheusQueryPort.PrometheusPoint::value,
                        (existing, replacement) -> existing
                ));
    }

    private long resolveCount(Map<Long, Double> countsByEpochSecond, Instant bucketEnd) {
        Double value = countsByEpochSecond.get(bucketEnd.getEpochSecond());
        return value == null ? 0L : Math.round(value);
    }
}
