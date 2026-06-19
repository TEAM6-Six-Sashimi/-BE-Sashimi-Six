package com.sashimi.ncs.infrastructure.publicdata;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.ncs")
public record NcsApiProperties(
        String baseUrl,
        String serviceKey,
        int numOfRows
) {}