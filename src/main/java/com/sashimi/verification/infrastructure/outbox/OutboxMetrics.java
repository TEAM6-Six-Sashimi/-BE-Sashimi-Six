package com.sashimi.verification.infrastructure.outbox;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OutboxMetrics {

    private final Counter sentCounter;
    private final Counter retryCounter;
    private final Counter failedCounter;

    public OutboxMetrics(MeterRegistry registry) {
        this.sentCounter = Counter.builder("email.outbox.sent")
                .description("Outbox 이메일 전송 성공 건수")
                .register(registry);
        this.retryCounter = Counter.builder("email.outbox.retry")
                .description("Outbox 이메일 재시도 건수")
                .register(registry);
        this.failedCounter = Counter.builder("email.outbox.failed")
                .description("Outbox 이메일 최종 실패 건수")
                .register(registry);
    }

    public void recordSent() {
        sentCounter.increment();
    }

    public void recordRetry() {
        retryCounter.increment();
    }

    public void recordFailed() {
        failedCounter.increment();
    }
}
