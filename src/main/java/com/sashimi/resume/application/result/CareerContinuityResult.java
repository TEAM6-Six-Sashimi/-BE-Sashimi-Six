package com.sashimi.resume.application.result;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

// 경력 연속성 점수 결과
// 0, 10, 20, 30점만 허용
public record CareerContinuityResult(
        int score
) {

    public CareerContinuityResult {
        boolean validScore =
                score == 0
                        || score == 10
                        || score == 20
                        || score == 30;

        if (!validScore) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }
    }

    public static CareerContinuityResult noCareer() {
        return new CareerContinuityResult(0);
    }

    public static CareerContinuityResult singleCareer() {
        return new CareerContinuityResult(30);
    }
}