package com.sashimi.credit.infrastructure.toss;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "toss.payments")
public record TossPaymentProperties(
        @NotBlank String baseUrl,
        @NotBlank String secretKey,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout
) {
}