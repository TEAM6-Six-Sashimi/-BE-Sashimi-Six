package com.sashimi.resume.application.service;

import com.sashimi.resume.domain.model.ResumeCareer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ResumeCareerDeduplicator {

    public List<ResumeCareer> deduplicate(
            List<ResumeCareer> careers
    ) {
        if (careers == null || careers.isEmpty()) {
            return List.of();
        }

        Map<String, ResumeCareer> uniqueCareers =
                new LinkedHashMap<>();

        for (ResumeCareer career : careers) {
            uniqueCareers.putIfAbsent(
                    buildDeduplicationKey(career),
                    career
            );
        }

        return uniqueCareers.values()
                .stream()
                .toList();
    }

    private String buildDeduplicationKey(
            ResumeCareer career
    ) {
        return String.join(
                "|",
                normalize(career.companyName()),
                String.valueOf(career.startYearMonth()),
                buildEndYearMonthKey(career),
                String.valueOf(career.currentlyEmployed()),
                String.valueOf(career.employmentType()),
                normalize(career.customEmploymentType()),
                normalize(career.jobTitle())
        );
    }

    private String buildEndYearMonthKey(
            ResumeCareer career
    ) {
        if (career.currentlyEmployed()) {
            return "CURRENT";
        }

        return String.valueOf(career.endYearMonth());
    }

    private String normalize(
            String value
    ) {
        if (value == null) {
            return "";
        }

        return value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}