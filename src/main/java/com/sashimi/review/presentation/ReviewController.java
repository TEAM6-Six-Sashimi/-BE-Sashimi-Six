package com.sashimi.review.presentation;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.review.application.command.WriteReviewCommand;
import com.sashimi.review.application.usecase.ReviewCommandUseCase;
import com.sashimi.review.presentation.api.request.WriteReviewRequest;
import com.sashimi.security.principal.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/courses/{courseId}/reviews")
@RequiredArgsConstructor
@Tag(name = "수강평 API", description = "수강평 작성 API")
public class ReviewController {

    private final ReviewCommandUseCase reviewCommandUseCase;

    @Operation(summary = "수강평 삭제", description = "본인이 작성한 수강평을 삭제합니다. 관리자는 타인의 수강평도 삭제 가능합니다.")
    @PatchMapping("/{reviewId}/delete")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !principal.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN);
        }
        reviewCommandUseCase.deleteReview(userId, reviewId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "수강평 작성", description = "수강 중인 강의에 평점과 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<Void> writeReview(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @Valid @RequestBody WriteReviewRequest request
    ) {
        if (!principal.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN);
        }
        reviewCommandUseCase.writeReview(
                new WriteReviewCommand(principal.getId(), courseId, request.rating(), request.content())
        );
        return ResponseEntity.noContent().build();
    }
}