package com.sashimi.payment.presentation.api;

import com.sashimi.payment.application.usecase.AdminPaymentQueryUseCase;
import com.sashimi.payment.presentation.api.response.AdminCoursePaymentHistoryResponse;
import com.sashimi.payment.presentation.api.response.AdminSubscriptionPaymentHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "관리자 결제 관리", description = "관리자 결제 내역 조회 API")
@SecurityRequirement(name = "bearerAuth")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final AdminPaymentQueryUseCase adminPaymentQueryUseCase;

    @Operation(
            summary = "관리자 강의 결제 내역 조회",
            description = """
                    관리자 페이지에서 전체 회원의 강의 결제 내역을 조회합니다.
                    
                    검색 조건:
                    - startDate, endDate: 결제일 범위 검색
                    - keyword: 회원명, 회원 ID, 주문번호 검색
                    - page: 0부터 시작
                    - size: 최대 100
                    
                    ROLE_ADMIN 권한이 필요합니다.
                    """
    )
    @GetMapping("/courses")
    public AdminCoursePaymentHistoryResponse getCoursePaymentHistory(
            @Parameter(description = "조회 시작일. 예: 2026-05-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "조회 종료일. 예: 2026-05-31")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(description = "회원명, 회원 ID, 주문번호 검색어")
            @RequestParam(required = false)
            String keyword,

            @Parameter(description = "페이지 번호. 0부터 시작")
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @Parameter(description = "페이지 크기. 최대 100")
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size
    ) {
        return AdminCoursePaymentHistoryResponse.from(
                adminPaymentQueryUseCase.getCoursePaymentHistory(
                        startDate,
                        endDate,
                        keyword,
                        page,
                        size
                )
        );
    }

    @Operation(
            summary = "관리자 구독권 결제 내역 조회",
            description = """
                    관리자 페이지에서 전체 회원의 AI 구독권 결제 내역을 조회합니다.
                    
                    검색 조건:
                    - startDate, endDate: 결제일 범위 검색
                    - keyword: 회원명, 회원 ID, 주문번호 검색
                    - planCode: MONTHLY 또는 ANNUAL
                    - page: 0부터 시작
                    - size: 최대 100
                    
                    ROLE_ADMIN 권한이 필요합니다.
                    """
    )
    @GetMapping("/subscriptions")
    public AdminSubscriptionPaymentHistoryResponse getSubscriptionPaymentHistory(
            @Parameter(description = "조회 시작일. 예: 2026-05-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "조회 종료일. 예: 2026-05-31")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(description = "회원명, 회원 ID, 주문번호 검색어")
            @RequestParam(required = false)
            String keyword,

            @Parameter(description = "구독 플랜. MONTHLY 또는 ANNUAL")
            @RequestParam(required = false)
            String planCode,

            @Parameter(description = "페이지 번호. 0부터 시작")
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @Parameter(description = "페이지 크기. 최대 100")
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size
    ) {
        return AdminSubscriptionPaymentHistoryResponse.from(
                adminPaymentQueryUseCase.getSubscriptionPaymentHistory(
                        startDate,
                        endDate,
                        keyword,
                        planCode,
                        page,
                        size
                )
        );
    }
}