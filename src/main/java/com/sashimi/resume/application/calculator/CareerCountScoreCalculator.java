package com.sashimi.resume.application.calculator;

import com.sashimi.resume.application.service.ResumeCareerDeduplicator;
import com.sashimi.resume.domain.model.ResumeCareer;
import org.springframework.stereotype.Component;

import java.util.List;

/** 경력 갯수 점수 계산기 */
@Component
public class CareerCountScoreCalculator {

    private final ResumeCareerDeduplicator resumeCareerDeduplicator;

    public CareerCountScoreCalculator(
            ResumeCareerDeduplicator resumeCareerDeduplicator
    ) {
        this.resumeCareerDeduplicator = resumeCareerDeduplicator;
    }

    public int calculate(
            List<ResumeCareer> careers
    ) {
        List<ResumeCareer> uniqueCareers =
                resumeCareerDeduplicator.deduplicate(
                        careers
                );

        if (uniqueCareers.isEmpty()) {
            return 0;
        }

        int careerCount = uniqueCareers.size();

        if (careerCount == 1) {
            return 15;
        }

        if (careerCount == 2) {
            return 25;
        }

        return 30;
    }
}