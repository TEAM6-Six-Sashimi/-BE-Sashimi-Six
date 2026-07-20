package com.sashimi.recommendation.infrastructure.web;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.port.JobPostingContentExtractor;
import com.sashimi.recommendation.domain.model.RecommendationInputType;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;

@Component
public class JsoupJobPostingContentExtractor implements JobPostingContentExtractor {

    private static final int TIMEOUT_MILLIS = 5000;
    private static final int MAX_TEXT_LENGTH = 12000;
    private static final int MAX_REDIRECT_COUNT = 3;

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

        String normalizedUrl = normalizeUrl(sourceUrl);
        normalizedUrl = normalizeSaraminUrl(normalizedUrl);
        validatePublicHttpUrl(normalizedUrl);

        try {
            Document document = fetchDocument(normalizedUrl);

            String text = extractReadableText(document);

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

    private String normalizeSaraminUrl(String sourceUrl) {
        try {
            URI uri = URI.create(sourceUrl);

            String host = uri.getHost();
            if (host == null
                    || !(host.equals("saramin.co.kr")
                    || host.equals("www.saramin.co.kr"))) {
                return sourceUrl;
            }

            String recIdx = extractQueryParam(uri.getRawQuery(), "rec_idx");
            if (recIdx == null || recIdx.isBlank()) {
                return sourceUrl;
            }

            if (!recIdx.matches("\\d+")) {
                return sourceUrl;
            }

            return "https://www.saramin.co.kr/zf_user/jobs/view?rec_idx=" + recIdx;
        } catch (Exception e) {
            return sourceUrl;
        }
    }

    private String extractQueryParam(String rawQuery, String name) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return null;
        }

        String[] params = rawQuery.split("&");

        for (String param : params) {
            String[] pair = param.split("=", 2);

            if (pair.length == 2 && pair[0].equals(name)) {
                return pair[1];
            }
        }

        return null;
    }

    private String extractReadableText(Document document) {
        document.select("script, style, noscript, svg, img").remove();

        String text = document.body() == null
                ? document.wholeText()
                : document.body().wholeText();

        return text
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private Document fetchDocument(String url) {
        String currentUrl = url;

        for (int redirectCount = 0; redirectCount <= MAX_REDIRECT_COUNT; redirectCount++) {
            try {
                validatePublicHttpUrl(currentUrl);

                Connection.Response response = Jsoup.connect(currentUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(TIMEOUT_MILLIS)
                        .followRedirects(false)
                        .execute();

                int statusCode = response.statusCode();

                if (isRedirect(statusCode)) {
                    String location = response.header("Location");

                    if (location == null || location.isBlank()) {
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
                    }

                    currentUrl = resolveRedirectUrl(currentUrl, location);
                    continue;
                }

                return response.parse();
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }

        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    private String normalizeUrl(String sourceUrl) {
        String trimmedUrl = sourceUrl.trim();

        if (trimmedUrl.startsWith("//")) {
            return "https:" + trimmedUrl;
        }

        if (!trimmedUrl.contains("://")) {
            return "https://" + trimmedUrl;
        }

        return trimmedUrl;
    }

    private String resolveRedirectUrl(String currentUrl, String location) {
        try {
            URI currentUri = URI.create(currentUrl);
            URI redirectedUri = currentUri.resolve(location);

            String redirectedUrl = redirectedUri.toString();
            validatePublicHttpUrl(redirectedUrl);

            return redirectedUrl;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private boolean isRedirect(int statusCode) {
        return statusCode == 301
                || statusCode == 302
                || statusCode == 303
                || statusCode == 307
                || statusCode == 308;
    }

    private String limit(String text) {
        if (text.length() <= MAX_TEXT_LENGTH) {
            return text;
        }

        return text.substring(0, MAX_TEXT_LENGTH);
    }

    private void validatePublicHttpUrl(String sourceUrl) {
        try {
            URI uri = URI.create(sourceUrl);

            String scheme = uri.getScheme();
            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http")
                    || scheme.equalsIgnoreCase("https"))) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }

            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }

            InetAddress address = InetAddress.getByName(uri.getHost());

            if (address.isLoopbackAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress()
                    || address.isAnyLocalAddress()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}