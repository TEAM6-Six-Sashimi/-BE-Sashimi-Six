package com.sashimi.resume.application.calculator;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

// 이력서 점수별 등급 변환 계산기
// 보완 필요, 보통, 양호, 우수
@Component
public class ResumeGradeCalculator {

    public String calculate(int score) {
        if (score < 0 || score > 100) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        if (score <= 60) {
            return "보완 필요";
        }

        if (score <= 79) {
            return "보통";
        }

        if (score <= 89) {
            return "양호";
        }

        return "우수";
    }
}