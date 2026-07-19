package com.sashimi.ai.infrastructure.fastapi.chatbot;

import com.sashimi.ai.infrastructure.fastapi.FastApiProperties;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * 진로상담 챗봇 FastAPI 클라이언트.
 *
 * <p>FastAPI가 질문 분류 → RAG 검색 → 답변 생성을 모두 처리하고, Spring은 결과만 받는다.
 * 서버 주소는 external.ai-server.base-url 설정을 따른다(cover-letter와 동일한 AI 서버).
 */
@Component
public class FastApiChatbotClient {

    private final RestClient restClient;

    public FastApiChatbotClient(
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

    public FastApiChatResponse chat(
            FastApiChatRequest request
    ) {
        try {
            return restClient.post()
                    .uri("/api/chatbot/messages")
                    .body(request)
                    .retrieve()
                    .body(FastApiChatResponse.class);
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        }
    }
}
