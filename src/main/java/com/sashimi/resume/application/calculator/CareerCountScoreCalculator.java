package com.sashimi.resume.application.calculator;

import com.sashimi.resume.domain.model.ResumeCareer;
import org.springframework.stereotype.Component;

import java.util.List;

/** 경력 갯수 점수 계산기 */
@Component
public class CareerCountScoreCalculator {

    public int calculate(
            List<ResumeCareer> careers
    ) {
        if (careers == null || careers.isEmpty()) {
            return 0;
        }

        int careerCount = careers.size();

        if (careerCount == 1) {
            return 15;
        }

        if (careerCount == 2) {
            return 25;
        }

        return 30;
    }
}