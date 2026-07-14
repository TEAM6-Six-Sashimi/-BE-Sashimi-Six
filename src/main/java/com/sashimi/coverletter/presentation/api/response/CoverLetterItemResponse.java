package com.sashimi.coverletter.presentation.api.response;

import com.sashimi.coverletter.domain.model.CoverLetterQuestion;

public record CoverLetterItemResponse(
        String questionKey,
        String questionTitle,
        String content,
        int maxLength,
        int displayOrder
) {

    public static CoverLetterItemResponse of(
            CoverLetterQuestion question,
            String content
    ) {
        return new CoverLetterItemResponse(
                question.name(),
                question.title(),
                content == null ? "" : content,
                question.maxLength(),
                question.displayOrder()
        );
    }
}