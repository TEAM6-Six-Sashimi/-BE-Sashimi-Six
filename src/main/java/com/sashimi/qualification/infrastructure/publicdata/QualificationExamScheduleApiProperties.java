package com.sashimi.qualification.infrastructure.publicdata;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.qualification-exam-schedule")
public record QualificationExamScheduleApiProperties(
        String baseUrl,
        String serviceKey,
        int numOfRows
) {
}