package com.sashimi.global.presentation.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/monitoring")
@Tag(name = "어드민 모니터링 API", description = "Grafana 대시보드 URL 조회 (ROLE_ADMIN 전용)")
@SecurityRequirement(name = "bearerAuth")
public class AdminMonitoringController {

    @Value("${grafana.url}")
    private String grafanaUrl;

    @Operation(summary = "Grafana 대시보드 URL 조회", description = "관리자 페이지에서 모니터링 링크를 여는 데 사용합니다. ROLE_ADMIN 권한 필요.")
    @GetMapping
    public ResponseEntity<Map<String, String>> getMonitoringUrl() {
        return ResponseEntity.ok(Map.of("grafanaUrl", grafanaUrl));
    }
}
