package com.sashimi.maintenance.presentation;

import com.sashimi.maintenance.MaintenanceModeService;
import com.sashimi.maintenance.presentation.response.MaintenanceStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Tag(name = "점검모드 상태", description = "프론트가 점검 페이지 노출 여부를 판단하기 위한 공개 상태 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/maintenance")
public class MaintenanceStatusController {

    private final MaintenanceModeService maintenanceModeService;

    // 프론트(미들웨어 등)가 매 페이지 진입마다 이 API를 부를 수 있어서, 짧은 캐시를
    // 응답 헤더에 실어 보낸다. 프론트가 직접 폴링 캐시 로직을 짤 필요 없이
    // 표준 HTTP 캐싱(브라우저/Next.js fetch 캐시/CDN)만으로 부하를 줄일 수 있다.
    // 점검모드 on/off 반영이 최대 5초 늦어질 수 있지만, 실제 점검/배포는
    // 이보다 훨씬 오래 지속되므로 문제되지 않는다.
    @Operation(summary = "점검모드 상태 조회 (공개, 인증 불필요, 5초 캐시)")
    @GetMapping("/status")
    public ResponseEntity<MaintenanceStatusResponse> getStatus() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofSeconds(5)).cachePublic())
                .body(new MaintenanceStatusResponse(maintenanceModeService.isEnabled(), maintenanceModeService.getMessage()));
    }
}
