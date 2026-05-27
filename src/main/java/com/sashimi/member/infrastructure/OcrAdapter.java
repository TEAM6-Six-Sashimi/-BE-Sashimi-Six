package com.sashimi.member.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sashimi.member.application.port.OcrPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class OcrAdapter implements OcrPort {

    @Value("${clova.ocr.secret-key}")
    private String secretKey;

    @Value("${clova.ocr.invoke-url}")
    private String invokeUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OcrResult extractCertificateInfo(byte[] fileBytes, String fileName) {
        try {
            String requestBody = buildRequestBody(fileBytes, fileName);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(invokeUrl))
                    .header("Content-Type", "application/json")
                    .header("X-OCR-SECRET", secretKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Clova OCR 호출 실패: status={}, body={}", response.statusCode(), response.body());
                return new OcrResult(null, null, null, false);
            }

            return parseOcrResponse(response.body());

        } catch (Exception e) {
            log.error("Clova OCR 호출 중 예외 발생", e);
            return new OcrResult(null, null, null, false);
        }
    }

    private String buildRequestBody(byte[] fileBytes, String fileName) {
        String format = "jpg";
        if (fileName != null) {
            String lower = fileName.toLowerCase();
            if (lower.endsWith(".png")) format = "png";
            else if (lower.endsWith(".pdf")) format = "pdf";
        }

        String base64 = Base64.getEncoder().encodeToString(fileBytes);

        return String.format("""
                {
                    "version": "V2",
                    "requestId": "%s",
                    "timestamp": %d,
                    "images": [
                        {
                            "format": "%s",
                            "data": "%s",
                            "name": "certificate"
                        }
                    ]
                }
                """, UUID.randomUUID(), System.currentTimeMillis(), format, base64);
    }

    private OcrResult parseOcrResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode fields = root.path("images").get(0).path("fields");

        StringBuilder fullText = new StringBuilder();
        for (JsonNode field : fields) {
            fullText.append(field.path("inferText").asText()).append(" ");
        }

        String text = fullText.toString();
        log.info("OCR 추출 텍스트: {}", text);

        String certName = extractCertName(text);
        String issuer = extractIssuer(text);
        LocalDate issueDate = extractIssueDate(text);
        boolean success = certName != null && issueDate != null;

        return new OcrResult(certName, issuer, issueDate, success);
    }

    private String extractCertName(String text) {
        String[] keywords = {"기술사", "기능장", "기사", "산업기사", "기능사", "준전문가"};
        String[] tokens = text.split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            for (String keyword : keywords) {
                if (tokens[i].contains(keyword)) {
                    // 앞 2개 토큰 + 현재 토큰 합치기
                    int start = Math.max(0, i - 2);
                    StringBuilder sb = new StringBuilder();
                    for (int j = start; j <= i; j++) {
                        if (sb.length() > 0) sb.append(" ");
                        sb.append(tokens[j]);
                    }
                    return sb.toString();
                }
            }
        }
        return null;
    }

    private String extractIssuer(String text) {
        if (text.contains("한국산업인력공단")) return "한국산업인력공단";
        if (text.contains("대한상공회의소")) return "대한상공회의소";
        if (text.contains("한국기술자격검정원")) return "한국기술자격검정원";
        if (text.contains("한국데이터산업진흥원")) return "한국데이터산업진흥원";
        return null;
    }

    private LocalDate extractIssueDate(String text) {
        // 띄어쓰기 포함한 "합격일자" 패턴
        Pattern passPattern = Pattern.compile(
                "합\\s*격\\s*일\\s*자\\s*(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일"
        );
        Matcher passMatcher = passPattern.matcher(text);
        if (passMatcher.find()) {
            return LocalDate.of(
                    Integer.parseInt(passMatcher.group(1)),
                    Integer.parseInt(passMatcher.group(2)),
                    Integer.parseInt(passMatcher.group(3))
            );
        }

        // 없으면 첫 번째 날짜
        Pattern pattern = Pattern.compile("(\\d{4})[년.\\-]\\s*(\\d{1,2})[월.\\-]\\s*(\\d{1,2})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return LocalDate.of(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
            );
        }
        return null;
    }
}