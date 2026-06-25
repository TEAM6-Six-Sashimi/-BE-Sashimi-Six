package com.sashimi.qualification.presentation.api;

import com.sashimi.qualification.application.service.QualificationExamScheduleSyncService;
import com.sashimi.qualification.infrastructure.publicdata.QualificationExamScheduleApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/qualifications/exam-schedules")
public class AdminQualificationExamScheduleController {

    private final QualificationExamScheduleApiClient apiClient;
    private final QualificationExamScheduleSyncService syncService;

    public AdminQualificationExamScheduleController(
            QualificationExamScheduleApiClient apiClient,
            QualificationExamScheduleSyncService syncService
    ) {
        this.apiClient = apiClient;
        this.syncService = syncService;
    }

    @PostMapping("/sync-test")
    public ResponseEntity<String> syncTest(
            @RequestParam int implYy,
            @RequestParam(defaultValue = "T") String qualgbCd,
            @RequestParam String jmCd,
            @RequestParam(defaultValue = "1") int pageNo
    ) {
        return ResponseEntity.ok(
                apiClient.fetchSchedules(
                        implYy,
                        qualgbCd,
                        jmCd,
                        pageNo
                )
        );
    }

    @PostMapping("/sync-all")
    public ResponseEntity<QualificationExamScheduleSyncAllResponse> syncAll(
            @RequestParam int implYy,
            @RequestParam(defaultValue = "T") String qualgbCd
    ) {
        int syncedCount = syncService.syncAll(
                implYy,
                qualgbCd
        );

        return ResponseEntity.ok(
                new QualificationExamScheduleSyncAllResponse(
                        implYy,
                        qualgbCd,
                        syncedCount
                )
        );
    }

    public record QualificationExamScheduleSyncAllResponse(
            int implYy,
            String qualgbCd,
            int syncedCount
    ) {
    }

    @PostMapping("/sync-by-name")
    public ResponseEntity<QualificationExamScheduleSyncByNameResponse> syncByName(
            @RequestParam String qualificationName,
            @RequestParam int implYy
    ) {
        int syncedCount = syncService.syncByQualificationName(
                qualificationName,
                implYy
        );

        return ResponseEntity.ok(
                new QualificationExamScheduleSyncByNameResponse(
                        qualificationName,
                        implYy,
                        syncedCount
                )
        );
    }

    public record QualificationExamScheduleSyncByNameResponse(
            String qualificationName,
            int implYy,
            int syncedCount
    ) {
    }
}