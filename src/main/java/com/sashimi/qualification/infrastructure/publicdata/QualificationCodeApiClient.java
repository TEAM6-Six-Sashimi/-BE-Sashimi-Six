package com.sashimi.qualification.infrastructure.publicdata;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
public class QualificationCodeApiClient {

    private final RestClient restClient;
    private final QualificationCodeApiProperties properties;

    public QualificationCodeApiClient(
            QualificationCodeApiProperties properties
    ) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public String fetchCodes() {
        byte[] responseBytes = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getList")
                        .queryParam("serviceKey", properties.serviceKey())
                        .build())
                .retrieve()
                .body(byte[].class);

        if (responseBytes == null) {
            return "";
        }

        return new String(
                responseBytes,
                StandardCharsets.UTF_8
        );
    }
}