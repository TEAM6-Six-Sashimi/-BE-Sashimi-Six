package com.sashimi.maintenance.presentation;

import com.sashimi.maintenance.MaintenanceModeService;
import com.sashimi.maintenance.presentation.request.MaintenanceEnableRequest;
import com.sashimi.maintenance.presentation.response.MaintenanceStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 점검모드", description = "서비스 점검모드 on/off 관리 API (ROLE_ADMIN 전용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/maintenance")
public class AdminMaintenanceController {

    private final MaintenanceModeService maintenanceModeService;

    @Operation(summary = "점검모드 상태 조회")
    @GetMapping
    public ResponseEntity<MaintenanceStatusResponse> getStatus() {
        return ResponseEntity.ok(
                new MaintenanceStatusResponse(maintenanceModeService.isEnabled(), maintenanceModeService.getMessage())
        );
    }

    @Operation(
            summary = "점검모드 켜기",
            description = "ROLE_ADMIN과 로그인/헬스체크를 제외한 모든 요청을 점검 응답(503)으로 차단한다."
    )
    @PostMapping("/enable")
    public ResponseEntity<MaintenanceStatusResponse> enable(
            @RequestBody(required = false) MaintenanceEnableRequest request
    ) {
        maintenanceModeService.enable(request != null ? request.message() : null);
        return ResponseEntity.ok(
                new MaintenanceStatusResponse(true, maintenanceModeService.getMessage())
        );
    }

    @Operation(summary = "점검모드 끄기")
    @PostMapping("/disable")
    public ResponseEntity<MaintenanceStatusResponse> disable() {
        maintenanceModeService.disable();
        return ResponseEntity.ok(
                new MaintenanceStatusResponse(false, maintenanceModeService.getMessage())
        );
    }
}
