package com.sashimi.ai.infrastructure.fastapi.coverletter;

import com.sashimi.ai.infrastructure.fastapi.FastApiProperties;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class FastApiCoverLetterClient {

    private final RestClient restClient;

    public FastApiCoverLetterClient(
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

    public FastApiCoverLetterReviewResponse review(
            FastApiCoverLetterReviewRequest request
    ) {
        try {
            return restClient.post()
                    .uri("/api/cover-letters/review")
                    .body(request)
                    .retrieve()
                    .body(FastApiCoverLetterReviewResponse.class);
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }
}