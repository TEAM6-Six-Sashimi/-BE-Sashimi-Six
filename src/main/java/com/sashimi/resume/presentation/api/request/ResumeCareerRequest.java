package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.ResumeCareer;

import java.time.YearMonth;

public record ResumeCareerRequest(
        String companyName,
        YearMonth startYearMonth,
        YearMonth endYearMonth,
        boolean currentlyEmployed,
        EmploymentType employmentType,
        String customEmploymentType,
        String jobTitle
) {

    public ResumeCareer toDomain() {
        return new ResumeCareer(
                companyName,
                startYearMonth,
                endYearMonth,
                currentlyEmployed,
                employmentType,
                customEmploymentType,
                jobTitle
        );
    }
}