package com.sashimi.review.presentation;

import com.sashimi.review.application.usecase.AdminReviewReportCommandUseCase;
import com.sashimi.review.application.usecase.AdminReviewReportQueryUseCase;
import com.sashimi.review.domain.model.ReviewReportStatus;
import com.sashimi.review.presentation.api.response.AdminReviewReportDetailResponse;
import com.sashimi.review.presentation.api.response.AdminReviewReportListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reviews/reports")
@RequiredArgsConstructor
@Tag(name = "관리자 수강평 신고 API", description = "관리자 수강평 신고 관리 API")
public class AdminReviewReportController {

    private final AdminReviewReportQueryUseCase adminReviewReportQueryUseCase;
    private final AdminReviewReportCommandUseCase adminReviewReportCommandUseCase;

    @Operation(summary = "신고된 수강평 목록 조회", description = "상태별 필터링 가능 (PENDING/PROCESSED). 파라미터 없으면 전체 조회.")
    @GetMapping
    public ResponseEntity<List<AdminReviewReportListResponse>> getReports(
            @RequestParam(required = false) ReviewReportStatus status
    ) {
        List<AdminReviewReportListResponse> responses = adminReviewReportQueryUseCase.getReports(status)
                .stream()
                .map(AdminReviewReportListResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "신고 상세 조회", description = "신고 상세 모달에 표시할 데이터를 조회합니다.")
    @GetMapping("/{reportId}")
    public ResponseEntity<AdminReviewReportDetailResponse> getReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(
                AdminReviewReportDetailResponse.from(adminReviewReportQueryUseCase.getReport(reportId))
        );
    }

    @Operation(summary = "신고된 수강평 삭제", description = "수강평을 삭제하고 신고를 처리됨으로 변경합니다.")
    @PatchMapping("/{reportId}/delete")
    public ResponseEntity<Void> deleteReportedReview(@PathVariable Long reportId) {
        adminReviewReportCommandUseCase.deleteReportedReview(reportId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "신고 반려", description = "신고를 반려하고 처리됨으로 변경합니다. 수강평은 유지됩니다.")
    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<Void> rejectReport(@PathVariable Long reportId) {
        adminReviewReportCommandUseCase.rejectReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
