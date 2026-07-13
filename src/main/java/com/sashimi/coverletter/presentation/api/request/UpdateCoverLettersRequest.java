package com.sashimi.coverletter.presentation.api.request;

import java.util.List;

public record UpdateCoverLettersRequest(
        List<CoverLetterItemRequest> items
) {
}