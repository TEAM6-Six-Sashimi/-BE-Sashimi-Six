package com.sashimi.coverletter.presentation.api;

import com.sashimi.coverletter.application.service.CoverLetterReviewService;
import com.sashimi.coverletter.presentation.api.response.CoverLetterReviewCreateResponse;
import com.sashimi.coverletter.presentation.api.response.CoverLetterReviewResultResponse;
import com.sashimi.coverletter.presentation.api.response.LatestCoverLetterReviewSummaryResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cover-letters")
public class CoverLetterReviewController {

    private final CoverLetterReviewService coverLetterReviewService;

    public CoverLetterReviewController(
            CoverLetterReviewService coverLetterReviewService
    ) {
        this.coverLetterReviewService = coverLetterReviewService;
    }

    @PostMapping("/review")
    public CoverLetterReviewCreateResponse review(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return coverLetterReviewService.review(
                principal.getId()
        );
    }

    @GetMapping("/reviews/latest/summary")
    public LatestCoverLetterReviewSummaryResponse getLatestSummary(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return coverLetterReviewService.getLatestSummary(
                principal.getId()
        );
    }

    @GetMapping("/reviews/{reviewId}")
    public CoverLetterReviewResultResponse getReviewDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long reviewId
    ) {
        return coverLetterReviewService.getReviewDetail(
                principal.getId(),
                reviewId
        );
    }
}