package com.sashimi.resume.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.calculator.ResumeScoreCalculator;
import com.sashimi.resume.application.result.CareerContinuityResult;
import com.sashimi.resume.application.result.ResumeScoreResult;
import com.sashimi.resume.application.result.ReviewResumeResult;
import com.sashimi.resume.application.result.SectionFeedbackResult;
import com.sashimi.resume.domain.model.Resume;
import org.springframework.stereotype.Component;

import java.util.List;

// 이력서 평가 전체 흐름 조립
// 순서: 경력 연속성 평가, 점수 계산, 피드백 생성
@Component
public class ResumeReviewProcessor {

    private final CareerContinuityEvaluator
            careerContinuityEvaluator;

    private final ResumeScoreCalculator
            resumeScoreCalculator;

    private final ResumeFeedbackGenerator
            resumeFeedbackGenerator;

    public ResumeReviewProcessor(
            CareerContinuityEvaluator careerContinuityEvaluator,
            ResumeScoreCalculator resumeScoreCalculator,
            ResumeFeedbackGenerator resumeFeedbackGenerator
    ) {
        this.careerContinuityEvaluator =
                careerContinuityEvaluator;
        this.resumeScoreCalculator =
                resumeScoreCalculator;
        this.resumeFeedbackGenerator =
                resumeFeedbackGenerator;
    }

    public ReviewResumeResult process(
            Resume resume,
            int certificateCount
    ) {
        if (resume == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (certificateCount < 0) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        CareerContinuityResult continuity =
                careerContinuityEvaluator.evaluate(
                        resume
                );

        ResumeScoreResult scoreResult =
                resumeScoreCalculator.calculate(
                        resume,
                        certificateCount,
                        continuity.score()
                );

        List<SectionFeedbackResult> feedbacks =
                resumeFeedbackGenerator.generate(
                        scoreResult
                );

        return new ReviewResumeResult(
                scoreResult,
                feedbacks
        );
    }
}