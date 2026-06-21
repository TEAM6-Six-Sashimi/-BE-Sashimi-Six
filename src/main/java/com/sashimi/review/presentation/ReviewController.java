package com.sashimi.review.presentation;

import com.sashimi.member.presentation.api.response.ApiResponse;
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
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        reviewCommandUseCase.deleteReview(userId, reviewId, isAdmin);
        return ResponseEntity.ok(ApiResponse.of("수강평이 삭제되었습니다."));
    }

    @Operation(summary = "수강평 작성", description = "수강 중인 강의에 평점과 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> writeReview(
            @PathVariable Long userId,
            @PathVariable Long courseId,
            @Valid @RequestBody WriteReviewRequest request
    ) {
        reviewCommandUseCase.writeReview(
                new WriteReviewCommand(userId, courseId, request.rating(), request.content())
        );
        return ResponseEntity.ok(ApiResponse.of("리뷰가 등록되었습니다."));
    }
}
