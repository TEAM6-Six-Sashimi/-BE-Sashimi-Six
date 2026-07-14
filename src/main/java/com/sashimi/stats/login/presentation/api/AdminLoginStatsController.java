package com.sashimi.stats.login.presentation.api;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.stats.login.application.usecase.AdminLoginStatsQueryUseCase;
import com.sashimi.stats.login.presentation.api.response.LoginStatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "어드민 로그인 통계 API", description = "로그인 수 통계 조회 API")
@RestController
@RequestMapping("/admin/stats/logins")
@RequiredArgsConstructor
public class AdminLoginStatsController {

    private final AdminLoginStatsQueryUseCase adminLoginStatsQueryUseCase;

    @Operation(
            summary = "로그인 수 통계 조회",
            description = """
                    period=hourly면 오늘의 시간대별(00시~23시) 로그인 수를,
                    period=daily면 이번 주(월~일)의 요일별 로그인 수를 반환합니다.
                    """
    )
    @GetMapping
    public ResponseEntity<LoginStatsResponse> getLoginStats(
            @RequestParam String period
    ) {
        return ResponseEntity.ok(
                LoginStatsResponse.from(
                        adminLoginStatsQueryUseCase.getStats(parsePeriod(period))
                )
        );
    }

    private AdminLoginStatsQueryUseCase.LoginStatsPeriod parsePeriod(String period) {
        try {
            return AdminLoginStatsQueryUseCase.LoginStatsPeriod.valueOf(period.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.STATS_INVALID_PERIOD);
        }
    }
}
