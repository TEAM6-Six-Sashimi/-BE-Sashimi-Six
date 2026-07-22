package com.sashimi.coverletter.presentation.api.request;

public record CoverLetterItemRequest(
        String questionKey,
        String content
) {
}