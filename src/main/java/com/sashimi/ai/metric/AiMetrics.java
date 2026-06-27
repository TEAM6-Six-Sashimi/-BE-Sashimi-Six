package com.sashimi.ai.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AiMetrics {

    public static final String FEATURE_JOB_POSTING_RECOMMENDATION = "JOB_POSTING_RECOMMENDATION";
    public static final String FEATURE_RESUME_REVIEW = "RESUME_REVIEW";

    public static final String REASON_SUBSCRIPTION_REQUIRED = "SUBSCRIPTION_REQUIRED";
    public static final String REASON_AI_CONSENT_REQUIRED = "AI_CONSENT_REQUIRED";

    private final MeterRegistry meterRegistry;

    public AiMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementRequestStarted(String feature) {
        Counter.builder("ai.request.started")
                .description("AI 기능 요청 시작 수")
                .tag("feature", feature)
                .register(meterRegistry)
                .increment();
    }

    public void incrementRequestSuccess(String feature) {
        Counter.builder("ai.request.success")
                .description("AI 기능 요청 성공 수")
                .tag("feature", feature)
                .register(meterRegistry)
                .increment();
    }

    public void incrementRequestFailed(String feature, String reason) {
        Counter.builder("ai.request.failed")
                .description("AI 기능 요청 실패 수")
                .tag("feature", feature)
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }

    public void incrementRequestBlocked(String feature, String reason) {
        Counter.builder("ai.request.blocked")
                .description("AI 기능 접근 차단 수")
                .tag("feature", feature)
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }

    public void recordRequestDuration(
            String feature,
            String status,
            long durationMillis
    ) {
        Timer.builder("ai.request.duration")
                .description("AI 기능 요청 처리 시간")
                .tag("feature", feature)
                .tag("status", status)
                .register(meterRegistry)
                .record(durationMillis, TimeUnit.MILLISECONDS);
    }

    public void incrementGeminiCallSuccess(String feature) {
        Counter.builder("ai.gemini.call.success")
                .description("Gemini API 호출 성공 수")
                .tag("feature", feature)
                .register(meterRegistry)
                .increment();
    }

    public void incrementGeminiCallFailed(String feature, String reason) {
        Counter.builder("ai.gemini.call.failed")
                .description("Gemini API 호출 실패 시")
                .tag("feature", feature)
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }

    public void incrementResponseParseFailed(String feature) {
        Counter.builder("ai.response.parse.failed")
                .description("AI 응답 JSON 파싱 실패 수")
                .tag("feature", feature)
                .register(meterRegistry)
                .increment();
    }
}