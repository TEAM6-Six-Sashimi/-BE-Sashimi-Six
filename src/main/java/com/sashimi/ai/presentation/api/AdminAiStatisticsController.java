package com.sashimi.ai.presentation.api;

import com.sashimi.ai.application.service.AiRequestStatisticsService;
import com.sashimi.ai.domain.model.AiUsagePeriod;
import com.sashimi.ai.presentation.api.response.AiRequestStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
@Tag(name = "관리자 AI 통계 API", description = "관리자 화면에서 AI 기능별 요청 수를 조회합니다.")
@SecurityRequirement(name = "bearerAuth")
public class AdminAiStatisticsController {

    private final AiRequestStatisticsService service;

    public AdminAiStatisticsController(
            AiRequestStatisticsService service
    ) {
        this.service = service;
    }

    @Operation(
            summary = "AI 기능 사용량 조회",
            description = "관리자가 시간별 또는 일별 AI 기능 요청 수를 조회합니다. ROLE_ADMIN 권한이 필요합니다."
    )
    @GetMapping("/ai-usage")
    public AiRequestStatisticsResponse getAiUsage(
            @RequestParam(defaultValue = "hourly")
            AiUsagePeriod period
    ) {
        return service.getAiUsage(period);
    }
}