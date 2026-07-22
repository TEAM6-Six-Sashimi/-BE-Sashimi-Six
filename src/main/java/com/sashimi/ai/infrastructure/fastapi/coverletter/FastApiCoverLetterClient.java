package com.sashimi.ai.infrastructure.fastapi.coverletter;

import com.sashimi.ai.infrastructure.fastapi.FastApiProperties;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Slf4j
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
            FastApiCoverLetterReviewResponse response =
                    restClient.post()
                            .uri("/api/cover-letters/review")
                            .body(request)
                            .retrieve()
                            .body(FastApiCoverLetterReviewResponse.class);

            if (response == null) {
                throw new BusinessException(
                        ErrorCode.AI_RESPONSE_EMPTY
                );
            }

            return response;
        } catch (RestClientResponseException exception) {
            log.warn(
                    "FastAPI 자기소개서 첨삭 호출 실패: statusCode={}, responseBody={}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString()
            );

            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        } catch (RestClientException exception) {
            log.warn(
                    "FastAPI 자기소개서 첨삭 연결 실패: reason={}",
                    exception.getClass().getSimpleName()
            );

            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn(
                    "FastAPI 자기소개서 첨삭 응답 파싱 실패: reason={}",
                    exception.getClass().getSimpleName()
            );

            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }
}