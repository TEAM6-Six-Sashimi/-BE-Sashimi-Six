package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.ResumeEducation;

import java.time.YearMonth;

public record ResumeEducationResponse(
        String schoolName,
        YearMonth startYearMonth,
        YearMonth endYearMonth,
        EducationDegree degree,
        String major,
        GraduationStatus graduationStatus,
        String minorOrResearch
) {

    public static ResumeEducationResponse from(
            ResumeEducation education
    ) {
        return new ResumeEducationResponse(
                education.schoolName(),
                education.startYearMonth(),
                education.endYearMonth(),
                education.degree(),
                education.major(),
                education.graduationStatus(),
                education.minorOrResearch()
        );
    }
}