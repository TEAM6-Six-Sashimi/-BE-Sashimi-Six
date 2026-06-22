package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.ResumeCareer;

import java.time.YearMonth;

public record ResumeCareerResponse(
        String companyName,
        YearMonth startYearMonth,
        YearMonth endYearMonth,
        boolean currentlyEmployed,
        EmploymentType employmentType,
        String customEmploymentType,
        String jobTitle
) {

    public static ResumeCareerResponse from(
            ResumeCareer career
    ) {
        return new ResumeCareerResponse(
                career.companyName(),
                career.startYearMonth(),
                career.endYearMonth(),
                career.currentlyEmployed(),
                career.employmentType(),
                career.customEmploymentType(),
                career.jobTitle()
        );
    }
}