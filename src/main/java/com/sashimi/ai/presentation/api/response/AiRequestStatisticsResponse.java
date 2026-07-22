package com.sashimi.ai.presentation.api.response;

import java.util.List;

public record AiRequestStatisticsResponse(
        String period,
        List<AiUsageDataResponse> data
) {
}