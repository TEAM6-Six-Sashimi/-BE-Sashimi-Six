package com.sashimi.qualification.infrastructure.publicdata;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.qualification-code")
public record QualificationCodeApiProperties(
        String baseUrl,
        String serviceKey
) {
}