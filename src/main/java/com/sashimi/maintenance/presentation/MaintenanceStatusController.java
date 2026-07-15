package com.sashimi.maintenance.presentation;

import com.sashimi.maintenance.MaintenanceModeService;
import com.sashimi.maintenance.presentation.response.MaintenanceStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "점검모드 상태", description = "프론트가 점검 페이지 노출 여부를 판단하기 위한 공개 상태 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/maintenance")
public class MaintenanceStatusController {

    private final MaintenanceModeService maintenanceModeService;

    @Operation(summary = "점검모드 상태 조회 (공개, 인증 불필요)")
    @GetMapping("/status")
    public ResponseEntity<MaintenanceStatusResponse> getStatus() {
        return ResponseEntity.ok(
                new MaintenanceStatusResponse(maintenanceModeService.isEnabled(), maintenanceModeService.getMessage())
        );
    }
}
