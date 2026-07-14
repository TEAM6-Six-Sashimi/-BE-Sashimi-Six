package com.sashimi.ai.infrastructure.openai;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
public class OpenAiTextClient {

    private final OpenAiProperties openAiProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public OpenAiTextClient(
            OpenAiProperties openAiProperties,
            ObjectMapper objectMapper
    ) {
        this.openAiProperties = openAiProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create(
                openAiProperties.baseUrl()
        );
    }

    public String generate(String prompt) {
        validateOpenAiSettings();

        if (prompt == null || prompt.isBlank()) {
            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        }

        long startedAt = System.currentTimeMillis();

        log.info(
                "OpenAI API 호출: model={}, promptLength={}",
                openAiProperties.model(),
                prompt.length()
        );

        try {
            Map<String, Object> requestBody = Map.of(
                    "model",
                    openAiProperties.model(),
                    "input",
                    prompt
            );

            String responseBody = restClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(
                            "Authorization",
                            "Bearer " + openAiProperties.apiKey()
                    )
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            String generatedText = extractGeneratedText(
                    responseBody
            );

            log.info(
                    "OpenAI API 호출 성공: model={}, elapsedMs={}, responseLength={}",
                    openAiProperties.model(),
                    System.currentTimeMillis() - startedAt,
                    generatedText == null ? 0 : generatedText.length()
            );

            return generatedText;
        } catch (RestClientResponseException exception) {
            log.error(
                    "OpenAI API 호출 실패: model={}, statusCode={}, responseBody={}, elapsedMs={}",
                    openAiProperties.model(),
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
            log.error(
                    "OpenAI API 호출 실패: model={}, elapsedMs={}",
                    openAiProperties.model(),
                    System.currentTimeMillis() - startedAt,
                    exception
            );
            throw new BusinessException(
                    ErrorCode.AI_API_CALL_FAILED
            );
        }
    }

    private String extractGeneratedText(
            String responseBody
    ) {
        try {
            JsonNode root = objectMapper.readTree(
                    responseBody
            );

            JsonNode outputTextNode = root.get(
                    "output_text"
            );

            if (outputTextNode != null
                    && outputTextNode.isTextual()
                    && !outputTextNode.asText().isBlank()) {
                return outputTextNode.asText();
            }

            JsonNode outputNode = root.get("output");

            if (outputNode != null
                    && outputNode.isArray()) {
                for (JsonNode itemNode : outputNode) {
                    JsonNode contentNode =
                            itemNode.get("content");

                    if (contentNode == null
                            || !contentNode.isArray()) {
                        continue;
                    }

                    for (JsonNode contentItem : contentNode) {
                        JsonNode textNode =
                                contentItem.get("text");

                        if (textNode != null
                                && textNode.isTextual()
                                && !textNode.asText().isBlank()) {
                            return textNode.asText();
                        }
                    }
                }
            }

            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_EMPTY
            );
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }

    private void validateOpenAiSettings() {
        String apiKey = openAiProperties.apiKey();
        String model = openAiProperties.model();
        String baseUrl = openAiProperties.baseUrl();

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