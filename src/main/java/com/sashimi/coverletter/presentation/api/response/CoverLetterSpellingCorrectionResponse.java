package com.sashimi.coverletter.presentation.api.response;

public record CoverLetterSpellingCorrectionResponse(
        String original,
        String corrected
) {
}