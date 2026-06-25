package com.sashimi.certificate.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Component
public class CodefTokenManager {

    @Value("${codef.client-id}")
    private String clientId;

    @Value("${codef.client-secret}")
    private String clientSecret;

    private static final String TOKEN_URL = "https://oauth.codef.io/oauth/token";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;
    private LocalDateTime expiredAt;

    public synchronized String getAccessToken() {
        if (accessToken == null || LocalDateTime.now().isAfter(expiredAt)) {
            reissue();
        }
        return accessToken;
    }

    private void reissue() {
        try {
            String auth = Base64.getEncoder()
                    .encodeToString((clientId + ":" + clientSecret).getBytes());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOKEN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + auth)
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&scope=read"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = objectMapper.readTree(response.body());

            accessToken = json.get("access_token").asText();
            expiredAt = LocalDateTime.now().plusDays(7);
            log.info("CODEF accessToken 발급 완료");
        } catch (Exception e) {
            log.error("CODEF accessToken 발급 실패", e);
            throw new RuntimeException("CODEF 토큰 발급 실패", e);
        }
    }
}
