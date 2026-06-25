package com.sashimi.recommendation.infrastructure.web;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.port.JobPostingContentExtractor;
import com.sashimi.recommendation.domain.model.RecommendationInputType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class JsoupJobPostingContentExtractor implements JobPostingContentExtractor {

    private static final int TIMEOUT_MILLIS = 5000;
    private static final int MAX_TEXT_LENGTH = 12000;

    @Override
    public String extract(
            RecommendationInputType inputType,
            String sourceUrl,
            String rawContent
    ) {
        if (inputType == RecommendationInputType.TEXT) {
            return extractFromText(rawContent);
        }

        if (inputType == RecommendationInputType.URL) {
            return extractFromUrl(sourceUrl);
        }

        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    private String extractFromText(String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return limit(rawContent.trim());
    }

    private String extractFromUrl(String sourceUrl) {
        if (sourceUrl == null || sourceUrl.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            Document document = Jsoup.connect(sourceUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(TIMEOUT_MILLIS)
                    .get();

            document.select("script, style, noscript").remove();

            String text = document.body() == null
                    ? document.text()
                    : document.body().text();

            if (text == null || text.isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }

            return limit(text.trim());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private String limit(String text) {
        if (text.length() <= MAX_TEXT_LENGTH) {
            return text;
        }

        return text.substring(0, MAX_TEXT_LENGTH);
    }
}