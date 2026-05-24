//package com.sashimi.resume.infrastructure.ai;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sashimi.ai.domain.model.AiPrompt;
//import com.sashimi.global.infrastructure.config.OpenAiProperties;
//import com.sashimi.resume.application.port.ResumeAiReviewPort;
//import com.sashimi.resume.application.port.ResumeAiReviewResult;
//import com.sashimi.resume.domain.model.Resume;
//import com.openai.client.OpenAIClient;
//import com.openai.models.responses.Response;
//import com.openai.models.responses.ResponseCreateParams;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
///**
// * OpenAI를 사용해서 이력서를 평가하는 Adapter.
// *
// * application 계층은 ResumeAiReviewPort만 알고,
// * 실제 OpenAI SDK 사용은 infrastructure 계층인 이 클래스가 담당한다.
// */
//@Component
//@RequiredArgsConstructor
//public class OpenAiResumeReviewAdapter implements ResumeAiReviewPort {
//
//    private final OpenAIClient openAIClient;
//    private final OpenAiProperties openAiProperties;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public ResumeAiReviewResult review(Resume resume, AiPrompt prompt) {
//        // 1. DB에서 가져온 프롬프트 템플릿에 이력서 내용을 넣는다.
//        String input = buildInput(resume, prompt);
//
//        // 2. OpenAI Responses API 요청 객체를 만든다.
//        ResponseCreateParams params = ResponseCreateParams.builder()
//                .model(openAiProperties.model())
//                .input(input)
//                .build();
//
//        // 3. OpenAI API를 호출한다.
//        Response response = openAIClient.responses().create(params);
//
//        // 4. 응답 텍스트를 꺼낸다.
//        String outputText = response.outputText();
//
//        // 5. AI가 반환한 JSON 문자열을 ResumeAiReviewResult로 변환한다.
//        return parseReviewResult(outputText);
//    }
//
//    private String buildInput(Resume resume, AiPrompt prompt) {
//        // system prompt와 user prompt를 하나의 입력으로 합친다.
//        // 추후 Structured Outputs를 적용하면 이 부분을 더 엄격하게 만들 수 있다.
//        String userPrompt = prompt.prompt()
//                .replace("{resumeContent}", resume.content());
//
//        return """
//                %s
//
//                아래 형식의 JSON으로만 응답하세요.
//                {
//                  "overallScore": 82,
//                  "strengths": "강점 설명",
//                  "weaknesses": "약점 설명",
//                  "suggestions": "개선 제안",
//                  "aiResult": "전체 평가 상세 내용"
//                }
//
//                %s
//                """.formatted(prompt.name(), userPrompt);
//    }
//
//    private ResumeAiReviewResult parseReviewResult(String outputText) {
//        try {
//            JsonNode root = objectMapper.readTree(outputText);
//
//            return new ResumeAiReviewResult(
//                    root.get("overallScore").decimalValue(),
//                    root.get("strengths").asText(),
//                    root.get("weaknesses").asText(),
//                    root.get("suggestions").asText(),
//                    root.get("aiResult").asText()
//            );
//        } catch (Exception e) {
//            throw new IllegalStateException("AI 이력서 평가 결과를 파싱할 수 없습니다.", e);
//        }
//    }
//}
