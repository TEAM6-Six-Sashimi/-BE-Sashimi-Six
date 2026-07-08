package com.sashimi.credit.presentation.api;

import com.sashimi.credit.application.usecase.AdminCreditQueryUseCase;
import com.sashimi.credit.presentation.api.response.AdminCreditChargeHistoryResponse;
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

@Tag(name = "관리자 크레딧 관리", description = "관리자 크레딧 충전 내역 조회 API")
@SecurityRequirement(name = "bearerAuth")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/credits")
public class AdminCreditController {

    private final AdminCreditQueryUseCase adminCreditQueryUseCase;

    @Operation(
            summary = "관리자 크레딧 충전 내역 조회",
            description = """
                    관리자 페이지에서 전체 회원의 크레딧 충전 내역을 조회합니다.
                    
                    검색 조건:
                    - startDate, endDate: 결제일 범위 검색
                    - keyword: 회원 ID, 주문번호 검색
                    - page: 0부터 시작
                    - size: 최대 100
                    
                    ROLE_ADMIN 권한이 필요합니다.
                    """
    )
    @GetMapping("/charges")
    public AdminCreditChargeHistoryResponse getCreditChargeHistory(
            @Parameter(description = "조회 시작일. 예: 2026-05-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "조회 종료일. 예: 2026-05-31")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(description = "회원 ID 또는 주문번호 검색어")
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
        return AdminCreditChargeHistoryResponse.from(
                adminCreditQueryUseCase.getCreditChargeHistory(
                        startDate,
                        endDate,
                        keyword,
                        page,
                        size
                )
        );
    }
}