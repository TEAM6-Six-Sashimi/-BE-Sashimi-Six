package com.sashimi.qualification.application.service;

import com.sashimi.qualification.infrastructure.persistence.QualificationCodeJpaEntity;
import com.sashimi.qualification.infrastructure.persistence.QualificationExamScheduleJpaEntity;
import com.sashimi.qualification.infrastructure.persistence.SpringDataQualificationCodeRepository;
import com.sashimi.qualification.infrastructure.persistence.SpringDataQualificationExamScheduleRepository;
import com.sashimi.qualification.infrastructure.publicdata.QualificationExamScheduleApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Transactional
public class QualificationExamScheduleSyncService {

    private static final String SUCCESS_CODE = "00";

    private final QualificationExamScheduleApiClient apiClient;
    private final SpringDataQualificationExamScheduleRepository repository;
    private final ObjectMapper objectMapper;
    private final SpringDataQualificationCodeRepository codeRepository;

    public QualificationExamScheduleSyncService(
            QualificationExamScheduleApiClient apiClient,
            SpringDataQualificationExamScheduleRepository repository,
            SpringDataQualificationCodeRepository codeRepository,
            ObjectMapper objectMapper
    ) {
        this.apiClient = apiClient;
        this.repository = repository;
        this.codeRepository = codeRepository;
        this.objectMapper = objectMapper;
    }

    public int syncAll(int implYy, String qualgbCd) {
        int pageNo = 1;
        int syncedCount = 0;

        while (true) {
            String responseText = apiClient.fetchSchedules(
                    implYy,
                    qualgbCd,
                    null,
                    pageNo
            );

            SyncPageResult pageResult = syncPage(responseText);
            syncedCount += pageResult.syncedCount();

            if (pageNo >= pageResult.totalPage()) {
                break;
            }

            pageNo++;
        }

        log.info(
                "Qualification exam schedules synced: implYy={}, qualgbCd={}, syncedCount={}",
                implYy,
                qualgbCd,
                syncedCount
        );

        return syncedCount;
    }

    public int syncByQualificationName(
            String qualificationName,
            int implYy
    ) {
        QualificationCodeJpaEntity code = codeRepository
                .findFirstByQualificationName(qualificationName)
                .or(() -> codeRepository
                        .findFirstByQualificationNameContainingOrderByQualificationNameAsc(
                                qualificationName
                        )
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Qualification code not found: " + qualificationName
                        )
                );

        int pageNo = 1;
        int syncedCount = 0;

        while (true) {
            String responseText = apiClient.fetchSchedules(
                    implYy,
                    code.getQualificationTypeCode(),
                    code.getJmCd(),
                    pageNo
            );

            SyncPageResult pageResult = syncPageByQualification(
                    responseText,
                    qualificationName,
                    code.getJmCd()
            );

            syncedCount += pageResult.syncedCount();

            if (pageNo >= pageResult.totalPage()) {
                break;
            }

            pageNo++;
        }

        return syncedCount;
    }

    private SyncPageResult syncPage(String responseText) {
        try {
            JsonNode root = objectMapper.readTree(responseText);

            String resultCode = root.path("header")
                    .path("resultCode")
                    .asText();

            String resultMsg = root.path("header")
                    .path("resultMsg")
                    .asText();

            if (!SUCCESS_CODE.equals(resultCode)) {
                throw new IllegalStateException(
                        "Qualification exam schedule API failed: "
                                + resultCode
                                + " / "
                                + resultMsg
                );
            }

            JsonNode body = root.path("body");
            JsonNode items = body.path("items");

            int totalCount = body.path("totalCount").asInt(0);
            int numOfRows = body.path("numOfRows").asInt(50);
            int totalPage = calculateTotalPage(totalCount, numOfRows);

            if (!items.isArray()) {
                return new SyncPageResult(0, totalPage);
            }

            int savedCount = 0;
            LocalDateTime syncedAt = LocalDateTime.now();

            for (JsonNode item : items) {
                upsertSchedule(item, syncedAt);
                savedCount++;
            }

            return new SyncPageResult(savedCount, totalPage);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to sync qualification exam schedules",
                    e
            );
        }
    }

    private SyncPageResult syncPageByQualification(
            String responseText,
            String qualificationName,
            String jmCd
    ) {
        try {
            JsonNode root = objectMapper.readTree(responseText);

            String resultCode = root.path("header")
                    .path("resultCode")
                    .asText();

            String resultMsg = root.path("header")
                    .path("resultMsg")
                    .asText();

            if (!SUCCESS_CODE.equals(resultCode)) {
                throw new IllegalStateException(
                        "Qualification exam schedule API failed: "
                                + resultCode
                                + " / "
                                + resultMsg
                );
            }

            JsonNode body = root.path("body");
            JsonNode items = body.path("items");

            int totalCount = body.path("totalCount").asInt(0);
            int numOfRows = body.path("numOfRows").asInt(50);
            int totalPage = calculateTotalPage(totalCount, numOfRows);

            if (!items.isArray()) {
                return new SyncPageResult(0, totalPage);
            }

            int savedCount = 0;
            LocalDateTime syncedAt = LocalDateTime.now();

            for (JsonNode item : items) {
                upsertScheduleByQualification(
                        item,
                        qualificationName,
                        jmCd,
                        syncedAt
                );
                savedCount++;
            }

            return new SyncPageResult(savedCount, totalPage);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to sync qualification exam schedules by qualification",
                    e
            );
        }
    }

    private void upsertSchedule(
            JsonNode item,
            LocalDateTime syncedAt
    ) {
        int implYear = item.path("implYy").asInt();
        int implSeq = item.path("implSeq").asInt();
        String qualgbCd = item.path("qualgbCd").asText();
        String qualgbNm = item.path("qualgbNm").asText();
        String description = item.path("description").asText();

        LocalDate docRegStartDate =
                parseDate(item.path("docRegStartDt").asText());
        LocalDate docRegEndDate =
                parseDate(item.path("docRegEndDt").asText());
        LocalDate docExamStartDate =
                parseDate(item.path("docExamStartDt").asText());
        LocalDate docExamEndDate =
                parseDate(item.path("docExamEndDt").asText());

        QualificationExamScheduleJpaEntity entity =
                repository.findByImplYearAndImplSeqAndQualificationTypeCodeAndDescription(
                                implYear,
                                implSeq,
                                qualgbCd,
                                description
                        )
                        .orElseGet(() ->
                                new QualificationExamScheduleJpaEntity(
                                        null,
                                        null,
                                        implYear,
                                        implSeq,
                                        qualgbCd,
                                        qualgbNm,
                                        description,
                                        docRegStartDate,
                                        docRegEndDate,
                                        docExamStartDate,
                                        docExamEndDate,
                                        syncedAt
                                )
                        );

        entity.updateFromSync(
                docRegStartDate,
                docRegEndDate,
                docExamStartDate,
                docExamEndDate,
                syncedAt
        );

        repository.save(entity);
    }

    private void upsertScheduleByQualification(
            JsonNode item,
            String qualificationName,
            String jmCd,
            LocalDateTime syncedAt
    ) {
        int implYear = item.path("implYy").asInt();
        int implSeq = item.path("implSeq").asInt();
        String qualgbCd = item.path("qualgbCd").asText();
        String qualgbNm = item.path("qualgbNm").asText();
        String description = item.path("description").asText();

        LocalDate docRegStartDate =
                parseDate(item.path("docRegStartDt").asText());
        LocalDate docRegEndDate =
                parseDate(item.path("docRegEndDt").asText());
        LocalDate docExamStartDate =
                parseDate(item.path("docExamStartDt").asText());
        LocalDate docExamEndDate =
                parseDate(item.path("docExamEndDt").asText());

        QualificationExamScheduleJpaEntity entity =
                repository.findByJmCdAndImplYearAndImplSeqAndDescription(
                                jmCd,
                                implYear,
                                implSeq,
                                description
                        )
                        .orElseGet(() ->
                                new QualificationExamScheduleJpaEntity(
                                        jmCd,
                                        qualificationName,
                                        implYear,
                                        implSeq,
                                        qualgbCd,
                                        qualgbNm,
                                        description,
                                        docRegStartDate,
                                        docRegEndDate,
                                        docExamStartDate,
                                        docExamEndDate,
                                        syncedAt
                                )
                        );

        entity.updateQualificationInfo(
                jmCd,
                qualificationName
        );

        entity.updateFromSync(
                docRegStartDate,
                docRegEndDate,
                docExamStartDate,
                docExamEndDate,
                syncedAt
        );

        repository.save(entity);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();

        if (!trimmed.matches("\\d{8}")) {
            return null;
        }

        return LocalDate.parse(
                trimmed,
                DateTimeFormatter.BASIC_ISO_DATE
        );
    }

    private int calculateTotalPage(
            int totalCount,
            int numOfRows
    ) {
        if (totalCount == 0) {
            return 1;
        }

        return (int) Math.ceil(
                (double) totalCount / numOfRows
        );
    }

    private record SyncPageResult(
            int syncedCount,
            int totalPage
    ) {
    }
}