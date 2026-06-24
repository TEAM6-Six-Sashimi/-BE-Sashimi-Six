package com.sashimi.resume.application.calculator;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.result.ResumeScoreResult;
import com.sashimi.resume.application.result.SectionScoreResult;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeReviewSection;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

// 이력서 항목별 점수 조합하여 최종 평가 점수 계산기
@Component
public class ResumeScoreCalculator {

    private final EducationScoreCalculator
            educationScoreCalculator;

    private final CareerCountScoreCalculator
            careerCountScoreCalculator;

    private final CareerPeriodScoreCalculator
            careerPeriodScoreCalculator;

    private final CertificateScoreCalculator
            certificateScoreCalculator;

    private final ResumeGradeCalculator
            resumeGradeCalculator;

    public ResumeScoreCalculator(
            EducationScoreCalculator educationScoreCalculator,
            CareerCountScoreCalculator careerCountScoreCalculator,
            CareerPeriodScoreCalculator careerPeriodScoreCalculator,
            CertificateScoreCalculator certificateScoreCalculator,
            ResumeGradeCalculator resumeGradeCalculator
    ) {
        this.educationScoreCalculator =
                educationScoreCalculator;
        this.careerCountScoreCalculator =
                careerCountScoreCalculator;
        this.careerPeriodScoreCalculator =
                careerPeriodScoreCalculator;
        this.certificateScoreCalculator =
                certificateScoreCalculator;
        this.resumeGradeCalculator =
                resumeGradeCalculator;
    }

    public ResumeScoreResult calculate(
            Resume resume,
            int verifiedCertificateCount,
            int careerContinuityScore
    ) {
        return calculate(
                resume,
                verifiedCertificateCount,
                careerContinuityScore,
                YearMonth.now()
        );
    }

    public ResumeScoreResult calculate(
            Resume resume,
            int verifiedCertificateCount,
            int careerContinuityScore,
            YearMonth currentYearMonth
    ) {
        validateContinuityScore(
                resume,
                careerContinuityScore
        );

        int educationScore =
                educationScoreCalculator.calculate(
                        resume.educations()
                );

        int careerCountScore =
                careerCountScoreCalculator.calculate(
                        resume.careers()
                );

        int careerPeriodScore =
                careerPeriodScoreCalculator.calculate(
                        resume.careers(),
                        currentYearMonth
                );

        int careerScore =
                careerCountScore
                        + careerPeriodScore
                        + careerContinuityScore;

        int certificateScore =
                certificateScoreCalculator.calculate(
                        verifiedCertificateCount
                );

        int overallScore = Math.round(
                (
                        educationScore
                                + careerScore
                                + certificateScore
                ) / 3.0f
        );

        SectionScoreResult education =
                SectionScoreResult.of(
                        ResumeReviewSection.EDUCATION,
                        educationScore,
                        resumeGradeCalculator.calculate(
                                educationScore
                        )
                );

        SectionScoreResult career =
                SectionScoreResult.of(
                        ResumeReviewSection.CAREER,
                        careerScore,
                        resumeGradeCalculator.calculate(
                                careerScore
                        )
                );

        SectionScoreResult certificate =
                SectionScoreResult.of(
                        ResumeReviewSection.CERTIFICATE,
                        certificateScore,
                        resumeGradeCalculator.calculate(
                                certificateScore
                        )
                );

        return new ResumeScoreResult(
                education,
                career,
                certificate,
                careerCountScore,
                careerPeriodScore,
                careerContinuityScore,
                overallScore,
                resumeGradeCalculator.calculate(
                        overallScore
                )
        );
    }

    private void validateContinuityScore(
            Resume resume,
            int careerContinuityScore
    ) {
        boolean validScore =
                careerContinuityScore == 0
                        || careerContinuityScore == 10
                        || careerContinuityScore == 20
                        || careerContinuityScore == 30;

        if (!validScore) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (resume.careers().isEmpty()
                && careerContinuityScore != 0) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (!resume.careers().isEmpty()
                && careerContinuityScore == 0) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }
    }
}