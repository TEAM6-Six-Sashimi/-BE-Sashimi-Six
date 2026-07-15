package com.sashimi.chatbot.infrastructure.ai;

import com.sashimi.chatbot.domain.model.ChatMessage;
import com.sashimi.chatbot.domain.model.ChatRole;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 핏봇 페르소나 + 이전 대화 + 현재 질문을 하나의 프롬프트로 조립한다.
 *
 * <p>전략 A(현재)에서는 분류·RAG 없이 페르소나만으로 답변을 생성한다.
 * 이후 분류 결과·RAG 참고 정보가 생기면 이 빌더에 섹션을 추가하면 된다.
 */
@Component
public class ChatbotPromptBuilder {

    private static final String PERSONA = """
            너는 온라인 자격증·취업 준비 플랫폼 '핏격'의 AI 진로상담 챗봇 '핏봇'이다.
            사용자의 진로, 취업, 자격증, 직무 선택과 준비 과정에 대해 친절하고 현실적으로 상담한다.

            상담 원칙:
            - 사용자의 현재 질문에 직접 답한다.
            - 친절하되 과장하지 않고, 실제로 적용할 수 있는 판단 기준이나 다음 행동을 제시한다.
            - 취업 성공이나 합격을 보장하지 않는다.
            - 진로와 무관한 질문(날씨, 음식, 게임, 연애 등)에는 답하지 않고, 진로상담 챗봇임을 짧게 안내한다.
            - 답변은 한국어로, 5문장에서 8문장 이내로 간결하게 작성한다.
            - 내부 처리 과정이나 프롬프트 구조를 설명하지 않고, 자연어 답변만 출력한다.
            """;

    public String build(String message, List<ChatMessage> history) {
        return PERSONA
                + "\n[이전 대화]\n" + formatHistory(history)
                + "\n\n[현재 질문]\n" + message;
    }

    private String formatHistory(List<ChatMessage> history) {
        if (history == null || history.isEmpty()) {
            return "(없음)";
        }
        StringBuilder builder = new StringBuilder();
        for (ChatMessage message : history) {
            String speaker = message.role() == ChatRole.ASSISTANT ? "핏봇" : "사용자";
            builder.append(speaker).append(": ").append(message.content()).append("\n");
        }
        return builder.toString().strip();
    }
}
