package com.sashimi.chatbot.infrastructure.ai;

import com.sashimi.chatbot.application.port.CareerChatPort;
import com.sashimi.chatbot.domain.model.ChatMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * gemini 프로파일이 없을 때 사용하는 대체 어댑터.
 *
 * <p>실제 AI를 호출하지 않고 고정 문구를 반환한다. gemini 키/프로파일 없이도
 * 앱이 부팅되고 프론트가 API 흐름을 테스트할 수 있게 한다.
 * (recommendation/resume 기능의 Stub 어댑터와 동일한 패턴)
 */
@Component
@Profile("local & !gemini")
public class StubChatbotAdapter implements CareerChatPort {

    @Override
    public String generateReply(String message, List<ChatMessage> history) {
        return "[테스트 응답] 안녕하세요, 진로상담 챗봇 핏봇입니다. "
                + "실제 AI 답변을 보려면 gemini 프로파일과 GEMINI_API_KEY 설정이 필요합니다.";
    }
}
