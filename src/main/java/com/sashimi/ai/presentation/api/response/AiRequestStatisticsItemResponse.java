package com.sashimi.ai.presentation.api.response;

public record AiRequestStatisticsItemResponse(
        String featureType,
        String label,
        long requestCount
) {
}