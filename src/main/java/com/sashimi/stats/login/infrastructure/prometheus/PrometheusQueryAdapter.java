package com.sashimi.stats.login.infrastructure.prometheus;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.stats.login.application.port.PrometheusQueryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Repository
public class PrometheusQueryAdapter implements PrometheusQueryPort {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    private final RestClient restClient;

    public PrometheusQueryAdapter(PrometheusProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        this.restClient = RestClient.builder()
                .baseUrl(properties.url())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public List<PrometheusPoint> queryRange(String promQuery, Instant start, Instant end, long stepSeconds) {
        PrometheusQueryRangeResponse response;

        try {
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/query_range")
                            .queryParam("query", promQuery)
                            .queryParam("start", start.getEpochSecond())
                            .queryParam("end", end.getEpochSecond())
                            .queryParam("step", stepSeconds)
                            .build())
                    .retrieve()
                    .body(PrometheusQueryRangeResponse.class);
        } catch (RestClientException e) {
            log.error("Prometheus 조회 실패: query={}", promQuery, e);
            throw new BusinessException(ErrorCode.STATS_SOURCE_UNAVAILABLE);
        }

        if (response == null || !"success".equals(response.status()) || response.data() == null) {
            log.error("Prometheus 응답이 올바르지 않음: query={}, response={}", promQuery, response);
            throw new BusinessException(ErrorCode.STATS_SOURCE_UNAVAILABLE);
        }

        return response.data().result().stream()
                .flatMap(result -> result.values().stream())
                .map(this::toPoint)
                .toList();
    }

    private PrometheusPoint toPoint(List<Object> rawValue) {
        double epochSeconds = ((Number) rawValue.get(0)).doubleValue();
        double value = Double.parseDouble((String) rawValue.get(1));
        return new PrometheusPoint(Instant.ofEpochSecond((long) epochSeconds), value);
    }
}
