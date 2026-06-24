package com.sashimi.resume.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.YearMonth;

public class ResumeEducation {

    private final String schoolName;
    private final YearMonth startYearMonth;
    private final YearMonth endYearMonth;
    private final GraduationStatus graduationStatus;
    private final String major;
    private final EducationDegree degree;
    private final String minorOrResearch;

    public ResumeEducation(
            String schoolName,
            YearMonth startYearMonth,
            YearMonth endYearMonth,
            GraduationStatus graduationStatus,
            String major,
            EducationDegree degree,
            String minorOrResearch
    ) {
        if (schoolName == null || schoolName.isBlank()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_EDUCATION
            );
        }

        if (startYearMonth == null || endYearMonth == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_EDUCATION
            );
        }

        if (endYearMonth.isBefore(startYearMonth)) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_EDUCATION
            );
        }

        if (graduationStatus == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_EDUCATION
            );
        }

        if (major == null || major.isBlank()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_EDUCATION
            );
        }

        if (degree == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_EDUCATION
            );
        }

        this.schoolName = schoolName.trim();
        this.startYearMonth = startYearMonth;
        this.endYearMonth = endYearMonth;
        this.graduationStatus = graduationStatus;
        this.major = major.trim();
        this.degree = degree;
        this.minorOrResearch = normalize(minorOrResearch);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String schoolName() {
        return schoolName;
    }

    public YearMonth startYearMonth() {
        return startYearMonth;
    }

    public YearMonth endYearMonth() {
        return endYearMonth;
    }

    public GraduationStatus graduationStatus() {
        return graduationStatus;
    }

    public String major() {
        return major;
    }

    public EducationDegree degree() {
        return degree;
    }

    public String minorOrResearch() {
        return minorOrResearch;
    }
}