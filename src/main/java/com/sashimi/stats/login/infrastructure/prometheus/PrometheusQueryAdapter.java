package com.sashimi.stats.login.infrastructure.prometheus;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.stats.login.application.port.PrometheusQueryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Repository
public class PrometheusQueryAdapter implements PrometheusQueryPort {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    private final RestClient restClient;
    private final String baseUrl;

    public PrometheusQueryAdapter(PrometheusProperties properties) {
        this.baseUrl = properties.url();

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public List<PrometheusPoint> queryRange(String promQuery, Instant start, Instant end, long stepSeconds) {
        // UriComponentsBuilder.encode()는 값 안의 '{', '}'를 URI 템플릿 변수 문법으로 오인해
        // 인코딩을 건너뛴다. PromQL의 '{job="sashimi"}' 같은 라벨 셀렉터가 그대로 남아
        // URISyntaxException을 일으키므로, promQuery는 빌더에 넘기기 전에 직접 인코딩해서
        // 빌더가 '{', '}'를 볼 일이 없게 만든다.
        String encodedQuery = UriUtils.encodeQueryParam(promQuery, StandardCharsets.UTF_8);
        URI uri = UriComponentsBuilder.fromUriString(baseUrl + "/api/v1/query_range")
                .queryParam("query", encodedQuery)
                .queryParam("start", start.getEpochSecond())
                .queryParam("end", end.getEpochSecond())
                .queryParam("step", stepSeconds)
                .build(true)
                .toUri();

        PrometheusQueryRangeResponse response;

        try {
            response = restClient.get()
                    .uri(uri)
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
