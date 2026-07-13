package com.sashimi.stats.login.infrastructure.prometheus;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "prometheus")
public record PrometheusProperties(String url) {
}
