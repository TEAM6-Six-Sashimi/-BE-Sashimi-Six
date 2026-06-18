package com.sashimi.credit.infrastructure.toss;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final TossPaymentProperties properties;

    public TossPaymentConfirmResponse confirm(String paymentKey, String orderId, Long amount) {
        try {
            return RestClient.builder()
                    .baseUrl(properties.baseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, authorizationHeader())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri("/v1/payments/confirm")
                    .body(new TossPaymentConfirmRequest(paymentKey, orderId, amount))
                    .retrieve()
                    .body(TossPaymentConfirmResponse.class);
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.CREDIT_EXTERNAL_PAYMENT_FAILED);
        }
    }

    private String authorizationHeader() {
        String raw = properties.secretKey() + ":";
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }
}