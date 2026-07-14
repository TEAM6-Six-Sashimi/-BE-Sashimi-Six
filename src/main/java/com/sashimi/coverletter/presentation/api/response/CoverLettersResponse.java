package com.sashimi.coverletter.presentation.api.response;

import java.util.List;

public record CoverLettersResponse(
        List<CoverLetterItemResponse> items
) {
}