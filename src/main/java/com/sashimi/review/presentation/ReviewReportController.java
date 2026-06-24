package com.sashimi.review.presentation;

import com.sashimi.review.application.command.ReportReviewCommand;
import com.sashimi.review.application.usecase.ReviewReportCommandUseCase;
import com.sashimi.review.presentation.api.request.ReportReviewRequest;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "수강평 신고 API", description = "수강평 신고 API")
public class ReviewReportController {

    private final ReviewReportCommandUseCase reviewReportCommandUseCase;

    @Operation(summary = "수강평 신고", description = "부적절한 수강평을 신고합니다.")
    @PostMapping("/{reviewId}/reports")
    public ResponseEntity<Void> reportReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ReportReviewRequest request
    ) {
        reviewReportCommandUseCase.reportReview(
                new ReportReviewCommand(principal.getId(), reviewId, request.category(), request.reason())
        );
        return ResponseEntity.noContent().build();
    }
}
