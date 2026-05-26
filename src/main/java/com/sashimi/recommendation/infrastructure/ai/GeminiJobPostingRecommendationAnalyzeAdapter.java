package com.sashimi.recommendation.infrastructure.ai;

import com.sashimi.ai.infrastructure.gemini.GeminiProperties;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzePort;
import com.sashimi.recommendation.application.port.JobPostingRecommendationAnalyzeResult;
import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RequiredSkillRecommendation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Profile("gemini")
public class GeminiJobPostingRecommendationAnalyzeAdapter
        implements JobPostingRecommendationAnalyzePort {

    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public GeminiJobPostingRecommendationAnalyzeAdapter(
            GeminiProperties geminiProperties,
            ObjectMapper objectMapper
    ) {
        this.geminiProperties = geminiProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create(geminiProperties.baseUrl());
    }

    @Override
    public JobPostingRecommendationAnalyzeResult analyze(JobPostingRecommendation recommendation) {
        String prompt = buildPrompt(recommendation);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        String responseBody = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/{model}:generateContent")
                        .queryParam("key", geminiProperties.apiKey())
                        .build(geminiProperties.model()))
                .body(requestBody)
                .retrieve()
                .body(String.class);

        String generatedText = extractGeneratedText(responseBody);

        return parseAnalysisResult(generatedText, recommendation.resumeBased());
    }

    private String buildPrompt(JobPostingRecommendation recommendation) {
        String jobPostingInput = switch (recommendation.inputType()) {
            case URL -> """
                    The user submitted a job posting URL.
                    URL: %s
                    
                    URL crawling is not implemented yet.
                    Infer a reasonable analysis only from the URL and general job posting context.
                    """.formatted(recommendation.sourceUrl());

            case TEXT -> """
                    The user submitted this job posting text:
                    
                    %s
                    """.formatted(recommendation.rawContent());
        };

        return """
                You are an AI assistant for an LMS career recommendation feature.
                
                Analyze the job posting and return ONLY valid JSON.
                Do not include markdown fences.
                Do not include explanation outside JSON.
                
                Rules:
                - Extract required skills from the job posting.
                - Recommend certificates related to the job role and required skills.
                - Recommend courses that can help the user fill missing skills.
                - If resumeBased is false, set matchRate to null and requiredSkills[].matched to null.
                - If resumeBased is true, estimate matchRate from 0 to 100 and set requiredSkills[].matched to true or false.
                - Return Korean text for course titles, certificate reasons, and recommendation reasons.
                
                resumeBased: %s
                
                Required JSON format:
                {
                  "jobTitle": "Frontend Developer",
                  "matchRate": 56,
                  "requiredSkills": [
                    {
                      "name": "React",
                      "category": "Frontend",
                      "matched": true
                    }
                  ],
                  "courses": [
                    {
                      "courseId": 1,
                      "title": "실무에서 바로 쓰는 GraphQL 완전 정복",
                      "instructor": "김민준",
                      "matchedSkill": "GraphQL",
                      "reason": "공고의 GraphQL 필수 역량을 보완하기 위한 강의입니다."
                    }
                  ],
                  "certificates": [
                    {
                      "certificationId": 1,
                      "name": "AWS Cloud Practitioner",
                      "reason": "AWS 클라우드 기초 역량을 증명하는 데 도움이 됩니다.",
                      "relatedSkills": ["AWS"],
                      "difficulty": "쉬움"
                    }
                  ]
                }
                
                Job posting input:
                %s
                """.formatted(
                recommendation.resumeBased(),
                jobPostingInput
        );
    }

    private String extractGeneratedText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            return root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract Gemini response text.", e);
        }
    }

    private JobPostingRecommendationAnalyzeResult parseAnalysisResult(
            String generatedText,
            boolean resumeBased
    ) {
        try {
            String jsonText = removeMarkdownFence(generatedText);
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
            throw new IllegalStateException("Failed to parse Gemini job posting analysis result.", e);
        }
    }

    private String removeMarkdownFence(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("```json", "")
                .replace("```", "")
                .trim();
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
