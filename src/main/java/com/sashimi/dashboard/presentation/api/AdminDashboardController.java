package com.sashimi.dashboard.presentation.api;

import com.sashimi.dashboard.application.usecase.AdminDashboardQueryUseCase;
import com.sashimi.dashboard.presentation.api.response.AdminDashboardStatisticsResponse;
import com.sashimi.dashboard.presentation.api.response.AdminDashboardSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 대시보드 API", description = "관리자 대시보드 요약 지표 조회 API")
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardQueryUseCase adminDashboardQueryUseCase;

    @Operation(
            summary = "관리자 대시보드 요약 조회",
            description = """
                    관리자 대시보드 상단 카드에 표시할 누적 지표를 조회합니다.
                    누적 총 매출은 완료된 크레딧 충전 총액입니다.
                    누적 순이익은 강의 결제 플랫폼 수수료 30%와 구독권 결제 금액의 합계입니다.
                    누적 정산 금액은 강의 결제 금액의 70%입니다.
                    """
    )
    @GetMapping("/summary")
    public ResponseEntity<AdminDashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(
                AdminDashboardSummaryResponse.from(
                        adminDashboardQueryUseCase.getSummary()
                )
        );
    }

    @Operation(
            summary = "관리자 대시보드 현황 요약 조회",
            description = """
                    관리자 대시보드 현황 요약 카드에 표시할 집계 수치를 조회합니다.
                    전체 회원 수는 수강생 수와 강사 수의 합계이며 관리자는 제외합니다.
                    수강생 수와 강사 수는 ACTIVE 상태 회원만 집계합니다.
                    전체 강의 수는 APPROVED 상태의 강의만 집계합니다.
                    """
    )
    @GetMapping("/statistics")
    public ResponseEntity<AdminDashboardStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(
                AdminDashboardStatisticsResponse.from(
                        adminDashboardQueryUseCase.getStatistics()
                )
        );
    }
}