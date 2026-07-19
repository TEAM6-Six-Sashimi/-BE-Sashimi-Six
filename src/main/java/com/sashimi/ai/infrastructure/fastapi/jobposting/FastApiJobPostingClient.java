package com.sashimi.ai.infrastructure.fastapi.jobposting;

import com.sashimi.ai.infrastructure.fastapi.FastApiProperties;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class FastApiJobPostingClient {

    private final RestClient restClient;

    public FastApiJobPostingClient(
            FastApiProperties properties
    ) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(
                Duration.ofSeconds(properties.getTimeoutSeconds())
        );
        requestFactory.setReadTimeout(
                Duration.ofSeconds(properties.getTimeoutSeconds())
        );

        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    public FastApiJobPostingAnalyzeResponse analyze(
            FastApiJobPostingAnalyzeRequest request
    ) {
        try {
            return restClient.post()
                    .uri("/api/job-postings/analyze")
                    .body(request)
                    .retrieve()
                    .body(FastApiJobPostingAnalyzeResponse.class);
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }
}