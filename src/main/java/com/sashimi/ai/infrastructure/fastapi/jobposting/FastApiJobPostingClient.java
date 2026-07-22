package com.sashimi.ai.infrastructure.fastapi.jobposting;

import com.sashimi.ai.infrastructure.fastapi.FastApiProperties;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;

@Slf4j
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
            FastApiJobPostingAnalyzeResponse response = restClient.post()
                    .uri("/api/job-postings/analyze")
                    .body(request)
                    .retrieve()
                    .body(FastApiJobPostingAnalyzeResponse.class);

            if (response == null) {
                throw new BusinessException(
                        ErrorCode.AI_RESPONSE_EMPTY
                );
            }

            return response;
        } catch (RestClientResponseException exception) {
            log.warn(
                    "FastAPI 채용공고 분석 호출 실패: statusCode={}, responseBodyLength={}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString() == null
                            ? 0
                            : exception.getResponseBodyAsString().length()
            );

            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        } catch (RestClientException exception) {
            log.warn(
                    "FastAPI 채용공고 분석 연결 실패: reason={}",
                    exception.getClass().getSimpleName()
            );

            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn(
                    "FastAPI 채용공고 분석 응답 파싱 실패: reason={}",
                    exception.getClass().getSimpleName()
            );

            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }
}