package com.sashimi.recommendation.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.infrastructure.gemini.GeminiResponseCleaner;
import com.sashimi.ai.infrastructure.gemini.GeminiTextClient;
import com.sashimi.ai.infrastructure.prompt.JobPostingRecommendationPromptBuilder;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.FitAnalysisCategory;
import com.sashimi.recommendation.domain.model.FitAnalysisItem;
import com.sashimi.recommendation.domain.model.FitStatus;
import com.sashimi.recommendation.domain.model.JobFitAnalysis;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile("gemini")
public class GeminiJobPostingRecommendationAnalyzeAdapter
        implements JobPostingRecommendationAnalyzePort {

    private final ObjectMapper objectMapper;
    private final JobPostingRecommendationPromptBuilder promptBuilder;
    private final GeminiTextClient geminiTextClient;

    public GeminiJobPostingRecommendationAnalyzeAdapter(
            ObjectMapper objectMapper,
            JobPostingRecommendationPromptBuilder promptBuilder,
            GeminiTextClient geminiTextClient
    ) {
        this.objectMapper = objectMapper;
        this.promptBuilder = promptBuilder;
        this.geminiTextClient = geminiTextClient;
    }

    @Override
    public JobPostingRecommendationAnalyzeResult analyze(
            JobPostingRecommendation recommendation,
            AiPrompt aiPrompt
    ) {
        String prompt = promptBuilder.build(recommendation, aiPrompt);

        log.info("채용공고 AI 추천 요청: recommendationId={}, inputType={}, resumeBased={}, promptLength={}",
                recommendation.recommendationId(),
                recommendation.inputType(),
                recommendation.resumeBased(),
                prompt == null ? 0 : prompt.length());

        String generatedText = geminiTextClient.generate(prompt);

        JobPostingRecommendationAnalyzeResult result =
                parseAnalysisResult(generatedText);

        log.info("채용공고 AI 추천 결과: recommendationId={}, certificateCount={}, courseCount={}",
                recommendation.recommendationId(),
                result.certificates().size(),
                result.courses().size());

        return result;
    }

    private JobPostingRecommendationAnalyzeResult parseAnalysisResult(String generatedText) {
        try {
            String jsonText = GeminiResponseCleaner.removeMarkdownFence(generatedText);
            JsonNode root = objectMapper.readTree(jsonText);

            JobPostingSummary summary = parseSummary(root.path("summary"));
            JobFitAnalysis fitAnalysis = parseFitAnalysis(root.path("fitAnalysis"));
            List<CertificateRecommendation> certificates = parseCertificates(root.path("certificates"));
            List<CourseRecommendation> courses = parseCourses(root.path("courses"));

            return new JobPostingRecommendationAnalyzeResult(
                    summary,
                    fitAnalysis,
                    certificates,
                    courses
            );
        } catch (Exception e) {
            log.error("채용공고 AI 추천 응답 파싱 실패: generatedTextLength={}",
                    generatedText == null ? 0 : generatedText.length(),
                    e);

            throw new BusinessException(ErrorCode.AI_RESPONSE_PARSE_FAILED);
        }
    }

    private JobPostingSummary parseSummary(JsonNode summaryNode) {
        return new JobPostingSummary(
                summaryNode.path("jobRole").asText(),
                parseStringArray(summaryNode.path("requiredQualifications")),
                parseStringArray(summaryNode.path("preferredQualifications")),
                summaryNode.path("experienceRequirement").asText(),
                summaryNode.path("mainTaskSummary").asText()
        );
    }

    private JobFitAnalysis parseFitAnalysis(JsonNode fitAnalysisNode) {
        return new JobFitAnalysis(
                parseFitAnalysisItem(
                        FitAnalysisCategory.EDUCATION,
                        fitAnalysisNode.path("education")
                ),
                parseFitAnalysisItem(
                        FitAnalysisCategory.CAREER,
                        fitAnalysisNode.path("career")
                ),
                parseFitAnalysisItem(
                        FitAnalysisCategory.CERTIFICATION,
                        fitAnalysisNode.path("certification")
                ),
                parseStringArray(fitAnalysisNode.path("overallComments"))
        );
    }

    private FitAnalysisItem parseFitAnalysisItem(
            FitAnalysisCategory category,
            JsonNode itemNode
    ) {
        return new FitAnalysisItem(
                category,
                parseFitStatus(itemNode.path("status").asText()),
                itemNode.path("requiredCondition").asText(),
                itemNode.path("userCondition").asText(),
                itemNode.path("comment").asText(),
                parseStringArray(itemNode.path("missingItems"))
        );
    }

    private FitStatus parseFitStatus(String value) {
        if (value == null || value.isBlank()) {
            return FitStatus.NOT_SATISFIED;
        }

        try {
            return FitStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return FitStatus.NOT_SATISFIED;
        }
    }

    private List<CertificateRecommendation> parseCertificates(JsonNode certificatesNode) {
        List<CertificateRecommendation> certificates = new ArrayList<>();

        if (!certificatesNode.isArray()) {
            return certificates;
        }

        for (JsonNode certificateNode : certificatesNode) {
            certificates.add(new CertificateRecommendation(
                    parseNullableLong(certificateNode.get("certificationId")),
                    certificateNode.path("name").asText(),
                    certificateNode.path("reason").asText(),
                    parseStringArray(certificateNode.path("relatedSkills")),
                    certificateNode.path("difficulty").asText()
            ));
        }

        return certificates;
    }

    private List<CourseRecommendation> parseCourses(JsonNode coursesNode) {
        List<CourseRecommendation> courses = new ArrayList<>();

        if (!coursesNode.isArray()) {
            return courses;
        }

        for (JsonNode courseNode : coursesNode) {
            courses.add(new CourseRecommendation(
                    parseNullableLong(courseNode.get("courseId")),
                    courseNode.path("title").asText(),
                    courseNode.path("instructor").asText(),
                    courseNode.path("matchedSkill").asText(),
                    courseNode.path("reason").asText()
            ));
        }

        return courses;
    }

    private Long parseNullableLong(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        return node.asLong();
    }

    private List<String> parseStringArray(JsonNode arrayNode) {
        List<String> values = new ArrayList<>();

        if (!arrayNode.isArray()) {
            return values;
        }

        for (JsonNode node : arrayNode) {
            values.add(node.asText());
        }

        return values;
    }
}