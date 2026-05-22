package com.sashimi.resume.application.port;

import java.math.BigDecimal;

/**
 * 외부 AI 평가 Adapter가 application 계층으로 전달하는 결과.
 *
 * HTTP 응답 DTO가 아니라,
 * OpenAI 호출 결과를 Service가 ResumeEvaluation으로 변환하기 위한 중간 결과 객체다.
 */
public record ResumeAiReviewResult(
        BigDecimal overallScore,
        String strengths,
        String weaknesses,
        String suggestions,
        String aiResult
) {
}
