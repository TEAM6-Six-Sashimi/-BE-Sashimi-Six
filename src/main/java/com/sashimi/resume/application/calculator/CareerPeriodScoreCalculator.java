package com.sashimi.resume.application.calculator;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.service.ResumeCareerDeduplicator;
import com.sashimi.resume.domain.model.ResumeCareer;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

// 이력서 항목 중 경력의 총 근무 개월 수 계산기
@Component
public class CareerPeriodScoreCalculator {

    private final ResumeCareerDeduplicator resumeCareerDeduplicator;

    public CareerPeriodScoreCalculator(
            ResumeCareerDeduplicator resumeCareerDeduplicator
    ) {
        this.resumeCareerDeduplicator = resumeCareerDeduplicator;
    }

    public int calculate(
            List<ResumeCareer> careers
    ) {
        return calculate(
                careers,
                YearMonth.now()
        );
    }

    public int calculate(
            List<ResumeCareer> careers,
            YearMonth currentYearMonth
    ) {
        List<ResumeCareer> uniqueCareers =
                resumeCareerDeduplicator.deduplicate(
                        careers
                );

        if (uniqueCareers.isEmpty()) {
            return 0;
        }

        if (currentYearMonth == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        long totalMonths = uniqueCareers.stream()
                .mapToLong(career ->
                        calculateMonths(
                                career,
                                currentYearMonth
                        )
                )
                .sum();

        if (totalMonths < 12) {
            return 10;
        }

        if (totalMonths < 36) {
            return 25;
        }

        if (totalMonths < 60) {
            return 35;
        }

        return 40;
    }

    private long calculateMonths(
            ResumeCareer career,
            YearMonth currentYearMonth
    ) {
        YearMonth endYearMonth =
                career.currentlyEmployed()
                        ? currentYearMonth
                        : career.endYearMonth();

        long months = ChronoUnit.MONTHS.between(
                career.startYearMonth(),
                endYearMonth
        );

        if (months < 0) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        return months;
    }
}