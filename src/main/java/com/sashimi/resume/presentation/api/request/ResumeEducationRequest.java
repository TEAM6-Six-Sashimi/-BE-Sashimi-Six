package com.sashimi.resume.presentation.api.request;

import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.ResumeEducation;

import java.time.YearMonth;

public record ResumeEducationRequest(
        String schoolName,
        YearMonth startYearMonth,
        YearMonth endYearMonth,
        EducationDegree degree,
        String major,
        GraduationStatus graduationStatus,
        String minorOrResearch
) {

    public ResumeEducation toDomain() {
        return new ResumeEducation(
                schoolName,
                startYearMonth,
                endYearMonth,
                degree,
                major,
                graduationStatus,
                minorOrResearch
        );
    }
}