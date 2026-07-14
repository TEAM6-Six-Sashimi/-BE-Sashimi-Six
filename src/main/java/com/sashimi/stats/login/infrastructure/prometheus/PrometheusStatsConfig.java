package com.sashimi.stats.login.infrastructure.prometheus;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PrometheusProperties.class)
public class PrometheusStatsConfig {
}
