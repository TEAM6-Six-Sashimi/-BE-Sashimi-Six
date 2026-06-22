package com.sashimi.resume.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.YearMonth;

public class ResumeEducation {

    private final String schoolName;
    private final YearMonth startYearMonth;
    private final YearMonth endYearMonth;
    private final EducationDegree degree;
    private final String major;
    private final GraduationStatus graduationStatus;
    private final String minorOrResearch;

    public ResumeEducation(
            String schoolName,
            YearMonth startYearMonth,
            YearMonth endYearMonth,
            EducationDegree degree,
            String major,
            GraduationStatus graduationStatus,
            String minorOrResearch
    ) {
        if (schoolName == null || schoolName.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (startYearMonth == null || endYearMonth == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (endYearMonth.isBefore(startYearMonth)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (degree == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (major == null || major.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (graduationStatus == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.schoolName = schoolName;
        this.startYearMonth = startYearMonth;
        this.endYearMonth = endYearMonth;
        this.degree = degree;
        this.major = major;
        this.graduationStatus = graduationStatus;
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

    public EducationDegree degree() {
        return degree;
    }

    public String major() {
        return major;
    }

    public GraduationStatus graduationStatus() {
        return graduationStatus;
    }

    public String minorOrResearch() {
        return minorOrResearch;
    }
}