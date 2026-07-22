package com.sashimi.stats.login.application.port;

import java.time.Instant;
import java.util.List;

public interface PrometheusQueryPort {

    List<PrometheusPoint> queryRange(String promQuery, Instant start, Instant end, long stepSeconds);

    record PrometheusPoint(Instant timestamp, double value) {
    }
}
