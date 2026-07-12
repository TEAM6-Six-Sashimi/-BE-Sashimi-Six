package com.sashimi.instructorapplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class InstructorApplicationAsyncConfig {

    @Bean(name = "instructorApplicationExecutor")
    public Executor instructorApplicationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("instructor-apply-");
        executor.initialize();
        return executor;
    }
}
