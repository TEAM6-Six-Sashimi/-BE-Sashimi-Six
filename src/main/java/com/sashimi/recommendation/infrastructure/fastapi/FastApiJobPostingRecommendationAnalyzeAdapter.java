package com.sashimi.recommendation.infrastructure.fastapi;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.infrastructure.fastapi.jobposting.FastApiJobPostingAnalyzeRequest;
import com.sashimi.ai.infrastructure.fastapi.jobposting.FastApiJobPostingAnalyzeResponse;
import com.sashimi.ai.infrastructure.fastapi.jobposting.FastApiJobPostingClient;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.port.JobPostingContentExtractor;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseSearchCriterion;
import com.sashimi.recommendation.domain.model.FitAnalysisCategory;
import com.sashimi.recommendation.domain.model.FitAnalysisItem;
import com.sashimi.recommendation.domain.model.FitStatus;
import com.sashimi.recommendation.domain.model.JobFitAnalysis;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@Profile("fastapi")
public class FastApiJobPostingRecommendationAnalyzeAdapter
        implements JobPostingRecommendationAnalyzePort {

    private final JobPostingContentExtractor contentExtractor;
    private final FastApiJobPostingClient fastApiJobPostingClient;

    public FastApiJobPostingRecommendationAnalyzeAdapter(
            JobPostingContentExtractor contentExtractor,
            FastApiJobPostingClient fastApiJobPostingClient
    ) {
        this.contentExtractor = contentExtractor;
        this.fastApiJobPostingClient = fastApiJobPostingClient;
    }

    @Override
    public JobPostingRecommendationAnalyzeResult analyze(
            JobPostingRecommendation recommendation,
            AiPrompt aiPrompt
    ) {
        String content = contentExtractor.extract(
                recommendation.inputType(),
                recommendation.sourceUrl(),
                recommendation.rawContent()
        );

        FastApiJobPostingAnalyzeRequest request =
                new FastApiJobPostingAnalyzeRequest(
                        content,
                        recommendation.resumeBased(),
                        recommendation.resumeContent()
                );

        log.info(
                "채용공고 FastAPI 분석 요청: recommendationId={}, inputType={}, resumeBased={}, contentLength={}",
                recommendation.recommendationId(),
                recommendation.inputType(),
                recommendation.resumeBased(),
                content.length()
        );

        FastApiJobPostingAnalyzeResponse response =
                fastApiJobPostingClient.analyze(request);

        return toAnalyzeResult(response);
    }

    private JobPostingRecommendationAnalyzeResult toAnalyzeResult(
            FastApiJobPostingAnalyzeResponse response
    ) {
        if (response == null || response.summary() == null) {
            throw new BusinessException(ErrorCode.AI_RESPONSE_PARSE_FAILED);
        }

        return new JobPostingRecommendationAnalyzeResult(
                toSummary(response.summary()),
                toFitAnalysis(response.fitAnalysis()),
                toCertificates(response.certificates()),
                List.of(),
                toCourseSearchCriteria(response.courseSearchCriteria())
        );
    }

    private JobPostingSummary toSummary(
            FastApiJobPostingAnalyzeResponse.Summary summary
    ) {
        return new JobPostingSummary(
                summary.jobRole(),
                nullToEmpty(summary.requiredQualifications()),
                nullToEmpty(summary.preferredQualifications()),
                summary.experienceRequirement(),
                summary.mainTaskSummary()
        );
    }

    private JobFitAnalysis toFitAnalysis(
            FastApiJobPostingAnalyzeResponse.FitAnalysis fitAnalysis
    ) {
        if (fitAnalysis == null) {
            return null;
        }

        return new JobFitAnalysis(
                toFitAnalysisItem(
                        FitAnalysisCategory.EDUCATION,
                        fitAnalysis.education()
                ),
                toFitAnalysisItem(
                        FitAnalysisCategory.CAREER,
                        fitAnalysis.career()
                ),
                toFitAnalysisItem(
                        FitAnalysisCategory.CERTIFICATION,
                        fitAnalysis.certification()
                ),
                nullToEmpty(fitAnalysis.overallComments())
        );
    }

    private FitAnalysisItem toFitAnalysisItem(
            FitAnalysisCategory category,
            FastApiJobPostingAnalyzeResponse.FitAnalysisItem item
    ) {
        if (item == null) {
            return new FitAnalysisItem(
                    category,
                    FitStatus.UNKNOWN,
                    "",
                    "",
                    "분석 결과가 제공되지 않았습니다.",
                    List.of()
            );
        }

        return new FitAnalysisItem(
                category,
                parseFitStatus(item.status()),
                item.required(),
                item.user(),
                item.comment(),
                nullToEmpty(item.missingItems())
        );
    }

    private List<CertificateRecommendation> toCertificates(
            List<FastApiJobPostingAnalyzeResponse.Certificate> certificates
    ) {
        return nullToEmpty(certificates)
                .stream()
                .map(certificate -> new CertificateRecommendation(
                        certificate.certificationId(),
                        certificate.name(),
                        certificate.reason(),
                        nullToEmpty(certificate.relatedSkills()),
                        certificate.difficulty()
                ))
                .toList();
    }

    private List<CourseSearchCriterion> toCourseSearchCriteria(
            List<FastApiJobPostingAnalyzeResponse.CourseSearchCriterion> criteria
    ) {
        return nullToEmpty(criteria)
                .stream()
                .map(criterion -> new CourseSearchCriterion(
                        criterion.recommendationType(),
                        criterion.keyword(),
                        criterion.reason(),
                        nullToEmpty(criterion.relatedSkills())
                ))
                .toList();
    }

    private FitStatus parseFitStatus(String value) {
        if (value == null || value.isBlank()) {
            return FitStatus.UNKNOWN;
        }

        try {
            return FitStatus.valueOf(
                    value.trim().toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            return FitStatus.UNKNOWN;
        }
    }

    private <T> List<T> nullToEmpty(List<T> values) {
        return values == null ? List.of() : values;
    }
}