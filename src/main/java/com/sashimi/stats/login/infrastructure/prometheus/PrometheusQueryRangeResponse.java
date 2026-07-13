package com.sashimi.stats.login.infrastructure.prometheus;

import java.util.List;
import java.util.Map;

public record PrometheusQueryRangeResponse(
        String status,
        PrometheusData data
) {

    public record PrometheusData(
            String resultType,
            List<PrometheusResult> result
    ) {
    }

    public record PrometheusResult(
            Map<String, String> metric,
            List<List<Object>> values
    ) {
    }
}
