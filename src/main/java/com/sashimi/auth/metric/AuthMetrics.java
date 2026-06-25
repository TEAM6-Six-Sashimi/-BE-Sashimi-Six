package com.sashimi.auth.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class AuthMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter loginSuccessCounter;
    private final Timer loginTimer;

    public AuthMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.loginSuccessCounter = Counter.builder("auth.login.success")
                .description("Number of successful login attempts")
                .register(meterRegistry);

        this.loginTimer = Timer.builder("auth.login.duration")
                .description("Time spent processing login requests")
                .register(meterRegistry);
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(loginTimer);
    }

    public void recordLoginSuccess() {
        loginSuccessCounter.increment();
    }

    // reason 태그는 제한된 값만 사용 — 메시지를 그대로 태그로 쓰면 시계열이 무한정 늘어남
    public void recordLoginFailed(String reason) {
        Counter.builder("auth.login.failed")
                .description("Number of failed login attempts")
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }
}
