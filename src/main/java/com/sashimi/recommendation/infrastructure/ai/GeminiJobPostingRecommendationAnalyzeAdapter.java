package com.sashimi.recommendation.infrastructure.ai;

import com.sashimi.ai.infrastructure.gemini.GeminiResponseCleaner;
import com.sashimi.ai.infrastructure.gemini.GeminiTextClient;
import com.sashimi.ai.infrastructure.prompt.JobPostingRecommendationPromptBuilder;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RequiredSkillRecommendation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

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
    public JobPostingRecommendationAnalyzeResult analyze(JobPostingRecommendation recommendation) {
        String prompt = promptBuilder.build(recommendation);

        String generatedText = geminiTextClient.generate(prompt);

        return parseAnalysisResult(generatedText, recommendation.resumeBased());
    }

    private JobPostingRecommendationAnalyzeResult parseAnalysisResult(
            String generatedText,
            boolean resumeBased
    ) {
        try {
            String jsonText = GeminiResponseCleaner.removeMarkdownFence(generatedText);
            JsonNode root = objectMapper.readTree(jsonText);

            String jobTitle = root.path("jobTitle").asText("Unknown Job");
            Integer matchRate = resumeBased ? parseNullableInteger(root.get("matchRate")) : null;

            List<RequiredSkillRecommendation> requiredSkills = parseRequiredSkills(
                    root.path("requiredSkills"),
                    resumeBased
            );
            List<CourseRecommendation> courses = parseCourses(root.path("courses"));
            List<CertificateRecommendation> certificates = parseCertificates(root.path("certificates"));

            return new JobPostingRecommendationAnalyzeResult(
                    jobTitle,
                    matchRate,
                    requiredSkills,
                    courses,
                    certificates
            );
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_RESPONSE_PARSE_FAILED);
        }
    }

    private Integer parseNullableInteger(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        return node.asInt();
    }

    private Boolean parseNullableBoolean(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        return node.asBoolean();
    }

    private List<RequiredSkillRecommendation> parseRequiredSkills(JsonNode skillsNode, boolean resumeBased) {
        List<RequiredSkillRecommendation> skills = new ArrayList<>();

        if (!skillsNode.isArray()) {
            return skills;
        }

        for (JsonNode skillNode : skillsNode) {
            skills.add(new RequiredSkillRecommendation(
                    skillNode.path("name").asText(),
                    skillNode.path("category").asText(),
                    resumeBased ? parseNullableBoolean(skillNode.get("matched")) : null
            ));
        }

        return skills;
    }

    private List<CourseRecommendation> parseCourses(JsonNode coursesNode) {
        List<CourseRecommendation> courses = new ArrayList<>();

        if (!coursesNode.isArray()) {
            return courses;
        }

        for (JsonNode courseNode : coursesNode) {
            courses.add(new CourseRecommendation(
                    courseNode.path("courseId").asLong(),
                    courseNode.path("title").asText(),
                    courseNode.path("instructor").asText(),
                    courseNode.path("matchedSkill").asText(),
                    courseNode.path("reason").asText()
            ));
        }

        return courses;
    }

    private List<CertificateRecommendation> parseCertificates(JsonNode certificatesNode) {
        List<CertificateRecommendation> certificates = new ArrayList<>();

        if (!certificatesNode.isArray()) {
            return certificates;
        }

        for (JsonNode certificateNode : certificatesNode) {
            certificates.add(new CertificateRecommendation(
                    certificateNode.path("certificationId").asLong(),
                    certificateNode.path("name").asText(),
                    certificateNode.path("reason").asText(),
                    parseStringArray(certificateNode.path("relatedSkills")),
                    certificateNode.path("difficulty").asText()
            ));
        }

        return certificates;
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