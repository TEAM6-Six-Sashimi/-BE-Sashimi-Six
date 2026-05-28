package com.sashimi.ai.infrastructure.gemini;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiTextClient {

    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public GeminiTextClient(
            GeminiProperties geminiProperties,
            ObjectMapper objectMapper
    ) {
        this.geminiProperties = geminiProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create(geminiProperties.baseUrl());
    }

    public String generate(String prompt) {
        validateGeminiApiKey();

        long startedAt = System.currentTimeMillis();

        log.info("🪢 Gemini API 호출: model={}, promptLength={}",
                geminiProperties.model(),
                prompt == null ? 0 : prompt.length());

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        try {
            String responseBody = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/{model}:generateContent")
                            .queryParam("key", geminiProperties.apiKey())
                            .build(geminiProperties.model()))
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            String generatedText = extractGeneratedText(responseBody);

            log.info("🪢 Gemini API 호출 성공: model={}, elapsedMs={}, responseLength={}",
                    geminiProperties.model(),
                    System.currentTimeMillis() - startedAt,
                    generatedText == null ? 0 : generatedText.length());

            return extractGeneratedText(responseBody);
        } catch (RestClientResponseException e) {
            log.error("🪢 Gemini API 호출 실패: model={}, statusCode={}, elapsedMs={}",
                    geminiProperties.model(),
                    e.getStatusCode(),
                    System.currentTimeMillis() - startedAt,
                    e);
            throw new BusinessException(ErrorCode.AI_API_CALL_FAILED);
        } catch (Exception e) {
            log.error("🪢 Gemini API 호출 실패: model={}, elapsedMs={}",
                    geminiProperties.model(),
                    System.currentTimeMillis() - startedAt,
                    e);
            throw new BusinessException(ErrorCode.AI_API_CALL_FAILED);
        }
    }

    private String extractGeneratedText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            String generatedText = root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            if (generatedText == null || generatedText.isBlank()) {
                throw new BusinessException(ErrorCode.AI_RESPONSE_EMPTY);
            }

            return generatedText;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_RESPONSE_PARSE_FAILED);
        }
    }

    private void validateGeminiApiKey() {
        String apiKey = geminiProperties.apiKey();

        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(ErrorCode.AI_API_KEY_MISSING);
        }

        if (apiKey.contains("${")) {
            throw new BusinessException(ErrorCode.AI_API_KEY_NOT_RESOLVED);
        }
    }
}