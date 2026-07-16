package com.sashimi.ncs.presentation.api;

import com.sashimi.ncs.application.service.NcsSyncService;
import com.sashimi.ncs.infrastructure.publicdata.NcsApiClient;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ncs")
@SecurityRequirement(name = "bearerAuth")
public class AdminNcsController {

    private final NcsApiClient ncsApiClient;
    private final NcsSyncService ncsSyncService;

    public AdminNcsController(NcsApiClient ncsApiClient, NcsSyncService ncsSyncService) {
        this.ncsApiClient = ncsApiClient;
        this.ncsSyncService = ncsSyncService;
    }

    @PostMapping("/sync")
    public ResponseEntity<NcsSyncResponse> sync(@RequestParam String dutyCd) {
        int syncedCount = ncsSyncService.syncByDutyCd(dutyCd);
        return ResponseEntity.ok(new NcsSyncResponse(dutyCd, syncedCount));
    }

    @PostMapping("/sync-test/compe-units")
    public ResponseEntity<String> syncCompeUnitTest(@RequestParam String dutyCd) {
        return ResponseEntity.ok(ncsApiClient.fetchNcsCompeUnitInfo(dutyCd, 1));
    }

    @PostMapping("/sync-test/codes")
    public ResponseEntity<String> syncCodeTest(@RequestParam(defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(ncsApiClient.fetchNcsCodeInfo(pageNo));
    }

    @PostMapping("/sync-test/duties")
    public ResponseEntity<String> syncDutyTest(@RequestParam String dutyCd) {
        return ResponseEntity.ok(ncsApiClient.fetchNcsDutyInfo(dutyCd, 1));
    }

    public record NcsSyncResponse(
            String dutyCd,
            int syncedCount
    ) {}
}