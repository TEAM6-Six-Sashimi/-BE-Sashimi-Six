package com.sashimi.resume.application.result;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.util.List;

// 이력서 항목별 점수와 전체 점수를 묶는 평가 점수 결과
public record ResumeScoreResult(
        SectionScoreResult education,
        SectionScoreResult career,
        SectionScoreResult certificate,
        int careerCountScore,
        int careerPeriodScore,
        int careerContinuityScore,
        int overallScore,
        String overallGrade
) {

    public ResumeScoreResult {
        if (education == null || career == null || certificate == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (overallScore < 0 || overallScore > 100) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (overallGrade == null || overallGrade.isBlank()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }
    }

    public List<SectionScoreResult> sectionScores() {
        return List.of(
                education,
                career,
                certificate
        );
    }
}