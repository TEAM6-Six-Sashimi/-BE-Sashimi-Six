package com.sashimi.ai.infrastructure.gemini;

import com.sashimi.ai.metric.AiMetrics;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@Profile("gemini")
public class GeminiTextClient {

    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final AiMetrics aiMetrics;

    public GeminiTextClient(
            GeminiProperties geminiProperties,
            ObjectMapper objectMapper,
            AiMetrics aiMetrics
    ) {
        this.geminiProperties = geminiProperties;
        this.objectMapper = objectMapper;
        this.aiMetrics = aiMetrics;
        this.restClient = RestClient.builder()
                .baseUrl(geminiProperties.baseUrl())
                .requestFactory(
                        createRequestFactory(
                                geminiProperties.timeoutSeconds()
                        )
                )
                .build();
    }

    public String generate(
            String prompt,
            String feature
    ) {
        validateGeminiSettings();

        if (prompt == null || prompt.isBlank()) {
            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        }

        String metricFeature = normalizeFeature(feature);

        long startedAt = System.currentTimeMillis();

        log.info(
                "Gemini API 호출: feature={}, model={}, promptLength={}, timeoutSeconds={}",
                metricFeature,
                geminiProperties.model(),
                prompt.length(),
                geminiProperties.timeoutSeconds()
        );

        try {
            Map<String, Object> requestBody = Map.of(
                    "model",
                    geminiProperties.model(),
                    "input",
                    prompt
            );

            String responseBody = restClient.post()
                    .uri("/interactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(
                            "x-goog-api-key",
                            geminiProperties.apiKey()
                    )
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            String generatedText = extractGeneratedText(
                    responseBody
            );

            aiMetrics.incrementProviderCallSuccess(
                    metricFeature,
                    AiMetrics.PROVIDER_GEMINI
            );

            log.info(
                    "Gemini API 호출 성공: feature={}, model={}, elapsedMs={}, responseLength={}",
                    metricFeature,
                    geminiProperties.model(),
                    System.currentTimeMillis() - startedAt,
                    generatedText == null ? 0 : generatedText.length()
            );

            return generatedText;
        } catch (RestClientResponseException exception) {
            aiMetrics.incrementProviderCallFailed(
                    metricFeature,
                    AiMetrics.PROVIDER_GEMINI,
                    exception.getStatusCode().toString()
            );

            log.error(
                    "Gemini API 호출 실패: feature={}, model={}, statusCode={}, responseBody={}, elapsedMs={}",
                    metricFeature,
                    geminiProperties.model(),
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString(),
                    System.currentTimeMillis() - startedAt,
                    exception
            );

            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            aiMetrics.incrementProviderCallFailed(
                    metricFeature,
                    AiMetrics.PROVIDER_GEMINI,
                    exception.getClass().getSimpleName()
            );

            log.error(
                    "Gemini API 호출 실패: feature={}, model={}, elapsedMs={}",
                    metricFeature,
                    geminiProperties.model(),
                    System.currentTimeMillis() - startedAt,
                    exception
            );

            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        }
    }

    private SimpleClientHttpRequestFactory createRequestFactory(
            int timeoutSeconds
    ) {
        int safeTimeoutSeconds =
                timeoutSeconds > 0
                        ? timeoutSeconds
                        : 30;

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        Duration timeout =
                Duration.ofSeconds(
                        safeTimeoutSeconds
                );

        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        return requestFactory;
    }

    private String extractGeneratedText(
            String responseBody
    ) {
        try {
            JsonNode root = objectMapper.readTree(
                    responseBody
            );

            // Interactions API의 raw REST 응답엔 output_text 필드가 없다
            // (공식 SDK가 편의상 계산해서 붙여주는 값). steps[].content[]에서
            // type=="text"인 항목들의 text를 직접 이어붙여야 한다.
            JsonNode steps = root.get("steps");
            StringBuilder generatedText = new StringBuilder();

            if (steps != null && steps.isArray()) {
                for (JsonNode step : steps) {
                    JsonNode contents = step.get("content");
                    if (contents == null || !contents.isArray()) {
                        continue;
                    }
                    for (JsonNode content : contents) {
                        JsonNode typeNode = content.get("type");
                        JsonNode textNode = content.get("text");
                        if (typeNode != null
                                && "text".equals(typeNode.asText())
                                && textNode != null
                                && textNode.isTextual()) {
                            generatedText.append(textNode.asText());
                        }
                    }
                }
            }

            if (generatedText.isEmpty()) {
                throw new BusinessException(
                        ErrorCode.AI_RESPONSE_EMPTY
                );
            }

            return generatedText.toString();
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }

    private String normalizeFeature(String feature) {
        if (feature == null || feature.isBlank()) {
            return "UNKNOWN";
        }

        return feature;
    }

    private void validateGeminiSettings() {
        String apiKey = geminiProperties.apiKey();
        String model = geminiProperties.model();
        String baseUrl = geminiProperties.baseUrl();

        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(
                    ErrorCode.AI_API_KEY_MISSING
            );
        }

        if (apiKey.contains("${")) {
            throw new BusinessException(
                    ErrorCode.AI_API_KEY_NOT_RESOLVED
            );
        }

        if (model == null || model.isBlank()) {
            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        }

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        }
    }
}