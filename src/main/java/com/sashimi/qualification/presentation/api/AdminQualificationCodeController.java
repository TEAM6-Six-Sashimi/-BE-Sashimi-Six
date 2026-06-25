package com.sashimi.qualification.presentation.api;

import com.sashimi.qualification.application.service.QualificationCodeSyncService;
import com.sashimi.qualification.infrastructure.publicdata.QualificationCodeApiClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/qualifications/codes")
public class AdminQualificationCodeController {

    private final QualificationCodeApiClient apiClient;
    private final QualificationCodeSyncService syncService;

    public AdminQualificationCodeController(
            QualificationCodeApiClient apiClient,
            QualificationCodeSyncService syncService
    ) {
        this.apiClient = apiClient;
        this.syncService = syncService;
    }

    @PostMapping(
            value = "/sync-test",
            produces = "application/xml; charset=UTF-8"
    )
    public ResponseEntity<String> syncTest() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/xml;charset=UTF-8"))
                .body(apiClient.fetchCodes());
    }

    @PostMapping("/sync")
    public ResponseEntity<QualificationCodeSyncResponse> sync() {
        int syncedCount = syncService.sync();

        return ResponseEntity.ok(
                new QualificationCodeSyncResponse(
                        syncedCount
                )
        );
    }

    public record QualificationCodeSyncResponse(
            int syncedCount
    ) {
    }
}