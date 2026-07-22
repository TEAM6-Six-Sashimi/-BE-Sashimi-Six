package com.sashimi.recommendation.infrastructure.persistence;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.FitAnalysisCategory;
import com.sashimi.recommendation.domain.model.FitAnalysisItem;
import com.sashimi.recommendation.domain.model.FitStatus;
import com.sashimi.recommendation.domain.model.JobFitAnalysis;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingSummary;
import com.sashimi.recommendation.domain.model.RecommendationAnalysisStatus;
import com.sashimi.recommendation.domain.model.RecommendationInputType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_posting_recommendations")
public class JobPostingRecommendationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "resume_id")
    private Long resumeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false, length = 20)
    private RecommendationInputType inputType;

    @Column(name = "source_url", length = 1000)
    private String sourceUrl;

    @Column(name = "raw_content", columnDefinition = "mediumtext")
    private String rawContent;

    @Column(name = "resume_content", columnDefinition = "mediumtext")
    private String resumeContent;

    @Column(name = "resume_based", nullable = false)
    private boolean resumeBased;

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_status", nullable = false, length = 30)
    private RecommendationAnalysisStatus analysisStatus;

    @Column(name = "summary_json", columnDefinition = "json")
    private String summaryJson;

    @Column(name = "fit_analysis_json", columnDefinition = "json")
    private String fitAnalysisJson;

    @Column(name = "courses_json", columnDefinition = "json")
    private String coursesJson;

    @Column(name = "certificates_json", columnDefinition = "json")
    private String certificatesJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected JobPostingRecommendationJpaEntity() {
    }

    Long getRecommendationId() {
        return recommendationId;
    }

    public static JobPostingRecommendationJpaEntity from(
            JobPostingRecommendation recommendation,
            ObjectMapper objectMapper
    ) {
        JobPostingRecommendationJpaEntity entity =
                new JobPostingRecommendationJpaEntity();

        entity.recommendationId = recommendation.recommendationId();
        entity.userId = recommendation.userId();
        entity.resumeId = recommendation.resumeId();
        entity.inputType = recommendation.inputType();
        entity.sourceUrl = recommendation.sourceUrl();
        entity.rawContent = recommendation.rawContent();
        entity.resumeContent = recommendation.resumeContent();
        entity.resumeBased = recommendation.resumeBased();
        entity.analysisStatus = recommendation.analysisStatus();
        entity.createdAt = recommendation.createdAt();

        entity.summaryJson = writeNullable(
                objectMapper,
                toSummaryJson(recommendation.summary())
        );
        entity.fitAnalysisJson = writeNullable(
                objectMapper,
                toFitAnalysisJson(recommendation.fitAnalysis())
        );
        entity.coursesJson = writeNullable(
                objectMapper,
                toCourseJsonList(recommendation.courses())
        );
        entity.certificatesJson = writeNullable(
                objectMapper,
                toCertificateJsonList(recommendation.certificates())
        );

        return entity;
    }

    public JobPostingRecommendation toDomain(
            ObjectMapper objectMapper
    ) {
        SummaryJson summaryJsonValue = readNullable(
                objectMapper,
                summaryJson,
                SummaryJson.class
        );

        FitAnalysisJson fitAnalysisJsonValue = readNullable(
                objectMapper,
                fitAnalysisJson,
                FitAnalysisJson.class
        );

        List<CourseJson> courseJsonValues = readList(
                objectMapper,
                coursesJson,
                new TypeReference<>() {
                }
        );

        List<CertificateJson> certificateJsonValues = readList(
                objectMapper,
                certificatesJson,
                new TypeReference<>() {
                }
        );

        return JobPostingRecommendation.restore(
                recommendationId,
                userId,
                resumeId,
                inputType,
                sourceUrl,
                rawContent,
                resumeContent,
                analysisStatus,
                resumeBased,
                toDomainSummary(summaryJsonValue),
                toDomainFitAnalysis(fitAnalysisJsonValue),
                toDomainCourses(courseJsonValues),
                toDomainCertificates(certificateJsonValues),
                createdAt
        );
    }

    private static SummaryJson toSummaryJson(
            JobPostingSummary summary
    ) {
        if (summary == null) {
            return null;
        }

        return new SummaryJson(
                summary.jobRole(),
                summary.requiredQualifications(),
                summary.preferredQualifications(),
                summary.experienceRequirement(),
                summary.mainTaskSummary()
        );
    }

    private static JobPostingSummary toDomainSummary(
            SummaryJson json
    ) {
        if (json == null) {
            return null;
        }

        return new JobPostingSummary(
                json.jobRole(),
                json.requiredQualifications(),
                json.preferredQualifications(),
                json.experienceRequirement(),
                json.mainTaskSummary()
        );
    }

    private static List<CourseJson> toCourseJsonList(
            List<CourseRecommendation> courses
    ) {
        if (courses == null) {
            return List.of();
        }

        return courses.stream()
                .map(course -> new CourseJson(
                        course.courseId(),
                        course.title(),
                        course.instructor(),
                        course.matchedSkill(),
                        course.reason()
                ))
                .toList();
    }

    private static List<CourseRecommendation> toDomainCourses(
            List<CourseJson> courses
    ) {
        if (courses == null) {
            return List.of();
        }

        return courses.stream()
                .map(course -> new CourseRecommendation(
                        course.courseId(),
                        course.title(),
                        course.instructor(),
                        course.matchedSkill(),
                        course.reason()
                ))
                .toList();
    }

    private static List<CertificateJson> toCertificateJsonList(
            List<CertificateRecommendation> certificates
    ) {
        if (certificates == null) {
            return List.of();
        }

        return certificates.stream()
                .map(certificate -> new CertificateJson(
                        certificate.certificationId(),
                        certificate.name(),
                        certificate.reason(),
                        certificate.relatedSkills(),
                        certificate.difficulty(),
                        certificate.nextExamDate(),
                        certificate.applicationStartDate(),
                        certificate.applicationEndDate()
                ))
                .toList();
    }

    private static List<CertificateRecommendation> toDomainCertificates(
            List<CertificateJson> certificates
    ) {
        if (certificates == null) {
            return List.of();
        }

        return certificates.stream()
                .map(certificate -> new CertificateRecommendation(
                        certificate.certificationId(),
                        certificate.name(),
                        certificate.reason(),
                        certificate.relatedSkills(),
                        certificate.difficulty(),
                        certificate.nextExamDate(),
                        certificate.applicationStartDate(),
                        certificate.applicationEndDate()
                ))
                .toList();
    }

    private static FitAnalysisJson toFitAnalysisJson(
            JobFitAnalysis fitAnalysis
    ) {
        if (fitAnalysis == null) {
            return null;
        }

        return new FitAnalysisJson(
                toFitAnalysisItemJson(fitAnalysis.education()),
                toFitAnalysisItemJson(fitAnalysis.career()),
                toFitAnalysisItemJson(fitAnalysis.certification()),
                fitAnalysis.overallComments()
        );
    }

    private static FitAnalysisItemJson toFitAnalysisItemJson(
            FitAnalysisItem item
    ) {
        if (item == null) {
            return null;
        }

        return new FitAnalysisItemJson(
                item.category(),
                item.status(),
                item.requiredCondition(),
                item.userCondition(),
                item.comment(),
                item.missingItems()
        );
    }

    private static JobFitAnalysis toDomainFitAnalysis(
            FitAnalysisJson json
    ) {
        if (json == null) {
            return null;
        }

        return new JobFitAnalysis(
                toDomainFitAnalysisItem(json.education()),
                toDomainFitAnalysisItem(json.career()),
                toDomainFitAnalysisItem(json.certification()),
                json.overallComments()
        );
    }

    private static FitAnalysisItem toDomainFitAnalysisItem(
            FitAnalysisItemJson json
    ) {
        if (json == null) {
            return null;
        }

        return new FitAnalysisItem(
                json.category(),
                json.status(),
                json.required(),
                json.user(),
                json.comment(),
                json.missingItems()
        );
    }

    private static String writeNullable(
            ObjectMapper objectMapper,
            Object value
    ) {
        if (value == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }

    private static <T> T readNullable(
            ObjectMapper objectMapper,
            String json,
            Class<T> type
    ) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(
                    json,
                    type
            );
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }

    private static <T> List<T> readList(
            ObjectMapper objectMapper,
            String json,
            TypeReference<List<T>> typeReference
    ) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(
                    json,
                    typeReference
            );
        } catch (Exception exception) {
            throw new BusinessException(
                    ErrorCode.AI_RESPONSE_PARSE_FAILED
            );
        }
    }

    private record SummaryJson(
            String jobRole,
            List<String> requiredQualifications,
            List<String> preferredQualifications,
            String experienceRequirement,
            String mainTaskSummary
    ) {
    }

    private record CourseJson(
            Long courseId,
            String title,
            String instructor,
            String matchedSkill,
            String reason
    ) {
    }

    private record CertificateJson(
            Long certificationId,
            String name,
            String reason,
            List<String> relatedSkills,
            String difficulty,
            LocalDate nextExamDate,
            LocalDate applicationStartDate,
            LocalDate applicationEndDate
    ) {
    }

    private record FitAnalysisJson(
            FitAnalysisItemJson education,
            FitAnalysisItemJson career,
            FitAnalysisItemJson certification,
            List<String> overallComments
    ) {
    }

    private record FitAnalysisItemJson(
            FitAnalysisCategory category,
            FitStatus status,
            String required,
            String user,
            String comment,
            List<String> missingItems
    ) {
    }
}