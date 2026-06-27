package com.sashimi.ai.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AiAsyncConfig {

    @Bean(name = "aiAnalysisExecutor")
    public Executor aiAnalysisExecutor(
            MeterRegistry meterRegistry
    ) {
        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("ai-analysis-");
        executor.initialize();

        registerMetrics(
                meterRegistry,
                executor
        );

        return executor;
    }

    private void registerMetrics(
            MeterRegistry meterRegistry,
            ThreadPoolTaskExecutor executor
    ) {
        Gauge.builder(
                        "ai.executor.active",
                        executor,
                        ThreadPoolTaskExecutor::getActiveCount
                )
                .description("현재 실행 중인 AI 분석 작업 수")
                .tag("executor", "aiAnalysisExecutor")
                .register(meterRegistry);

        Gauge.builder(
                        "ai.executor.pool.size",
                        executor,
                        ThreadPoolTaskExecutor::getPoolSize
                )
                .description("현재 AI 분석 ThreadPool 크기")
                .tag("executor", "aiAnalysisExecutor")
                .register(meterRegistry);

        Gauge.builder(
                        "ai.executor.queue.size",
                        executor,
                        target -> {
                            ThreadPoolExecutor threadPoolExecutor =
                                    target.getThreadPoolExecutor();

                            if (threadPoolExecutor == null) {
                                return 0;
                            }

                            return threadPoolExecutor
                                    .getQueue()
                                    .size();
                        }
                )
                .description("대기 중인 AI 분석 작업 수")
                .tag("executor", "aiAnalysisExecutor")
                .register(meterRegistry);

        Gauge.builder(
                        "ai.executor.completed",
                        executor,
                        target -> {
                            ThreadPoolExecutor threadPoolExecutor =
                                    target.getThreadPoolExecutor();

                            if (threadPoolExecutor == null) {
                                return 0;
                            }

                            return threadPoolExecutor
                                    .getCompletedTaskCount();
                        }
                )
                .description("완료된 AI 분석 작업 수")
                .tag("executor", "aiAnalysisExecutor")
                .register(meterRegistry);
    }
}