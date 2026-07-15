package com.sashimi.chatbot.infrastructure.ai;

import com.sashimi.ai.infrastructure.gemini.GeminiTextClient;
import com.sashimi.chatbot.application.port.CareerChatPort;
import com.sashimi.chatbot.domain.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Gemini 직접 호출 방식의 챗봇 어댑터(전략 A).
 *
 * <p>ai 패키지의 공용 {@link GeminiTextClient}를 재사용한다.
 * 프롬프트를 조립해 Gemini에 넘기고 생성된 자연어 답변을 그대로 반환한다.
 * (분류·RAG는 이후 FastAPI 어댑터로 교체하면서 도입한다.)
 */
@Slf4j
@Component
@Profile("gemini")
public class GeminiChatbotAdapter implements CareerChatPort {

    /** AiMetrics 태그용 기능 이름. */
    private static final String FEATURE_CAREER_CHAT = "CAREER_CHAT";

    private final ChatbotPromptBuilder promptBuilder;
    private final GeminiTextClient geminiTextClient;

    public GeminiChatbotAdapter(
            ChatbotPromptBuilder promptBuilder,
            GeminiTextClient geminiTextClient
    ) {
        this.promptBuilder = promptBuilder;
        this.geminiTextClient = geminiTextClient;
    }

    @Override
    public String generateReply(String message, List<ChatMessage> history) {
        String prompt = promptBuilder.build(message, history);

        log.info("[Chatbot] Gemini 답변 요청 - historyCount={}, promptLength={}",
                history.size(), prompt.length());

        return geminiTextClient.generate(prompt, FEATURE_CAREER_CHAT);
    }
}
