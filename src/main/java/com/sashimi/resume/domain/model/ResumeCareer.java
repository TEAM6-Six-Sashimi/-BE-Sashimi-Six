package com.sashimi.resume.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.YearMonth;

public class ResumeCareer {

    private final String companyName;
    private final YearMonth startYearMonth;
    private final YearMonth endYearMonth;
    private final boolean currentlyEmployed;
    private final EmploymentType employmentType;
    private final String customEmploymentType;
    private final String jobTitle;

    public ResumeCareer(
            String companyName,
            YearMonth startYearMonth,
            YearMonth endYearMonth,
            boolean currentlyEmployed,
            EmploymentType employmentType,
            String customEmploymentType,
            String jobTitle
    ) {
        if (companyName == null || companyName.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (startYearMonth == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (!currentlyEmployed && endYearMonth == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (endYearMonth != null && endYearMonth.isBefore(startYearMonth)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (employmentType == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (employmentType == EmploymentType.OTHER
                && (customEmploymentType == null || customEmploymentType.isBlank())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (jobTitle == null || jobTitle.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.companyName = companyName;
        this.startYearMonth = startYearMonth;
        this.endYearMonth = currentlyEmployed ? null : endYearMonth;
        this.currentlyEmployed = currentlyEmployed;
        this.employmentType = employmentType;
        this.customEmploymentType = employmentType == EmploymentType.OTHER
                ? customEmploymentType.trim()
                : null;
        this.jobTitle = jobTitle;
    }

    public String companyName() {
        return companyName;
    }

    public YearMonth startYearMonth() {
        return startYearMonth;
    }

    public YearMonth endYearMonth() {
        return endYearMonth;
    }

    public boolean currentlyEmployed() {
        return currentlyEmployed;
    }

    public EmploymentType employmentType() {
        return employmentType;
    }

    public String customEmploymentType() {
        return customEmploymentType;
    }

    public String jobTitle() {
        return jobTitle;
    }
}