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

@Component
public class ResumeReviewProcessor {

    private final CareerContinuityEvaluator careerContinuityEvaluator;
    private final ResumeScoreCalculator resumeScoreCalculator;
    private final ResumeFeedbackGenerator resumeFeedbackGenerator;

    public ResumeReviewProcessor(
            CareerContinuityEvaluator careerContinuityEvaluator,
            ResumeScoreCalculator resumeScoreCalculator,
            ResumeFeedbackGenerator resumeFeedbackGenerator
    ) {
        this.careerContinuityEvaluator = careerContinuityEvaluator;
        this.resumeScoreCalculator = resumeScoreCalculator;
        this.resumeFeedbackGenerator = resumeFeedbackGenerator;
    }

    public ReviewResumeResult process(
            Resume resume
    ) {
        if (resume == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        int certificateCount = countCertifications(resume);

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

    private int countCertifications(
            Resume resume
    ) {
        if (resume.certifications() == null) {
            return 0;
        }

        return resume.certifications().size();
    }
}