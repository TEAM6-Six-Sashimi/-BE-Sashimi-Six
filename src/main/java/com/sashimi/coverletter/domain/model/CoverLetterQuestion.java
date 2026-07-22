package com.sashimi.coverletter.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.util.Arrays;

public enum CoverLetterQuestion {

    SELF_INTRODUCTION(
            "자기 소개",
            500,
            1
    ),
    PERSONALITY(
            "본인 성격의 장점과 단점",
            500,
            2
    ),
    PROJECT_EXPERIENCE(
            "프로젝트 경험",
            500,
            3
    ),
    PROBLEM_SOLVING(
            "문제 해결 경험",
            300,
            4
    ),
    CONFLICT_RESOLUTION(
            "상사와 의견이 다를 경우",
            300,
            5
    );

    private final String title;
    private final int maxLength;
    private final int displayOrder;

    CoverLetterQuestion(
            String title,
            int maxLength,
            int displayOrder
    ) {
        this.title = title;
        this.maxLength = maxLength;
        this.displayOrder = displayOrder;
    }

    public String title() {
        return title;
    }

    public int maxLength() {
        return maxLength;
    }

    public int displayOrder() {
        return displayOrder;
    }

    public static CoverLetterQuestion from(String questionKey) {
        return Arrays.stream(values())
                .filter(question -> question.name().equals(questionKey))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.COVER_LETTER_INVALID_QUESTION
                ));
    }
}