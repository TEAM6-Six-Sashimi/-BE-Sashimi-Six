package com.sashimi.chatbot.infrastructure.ai;

import com.sashimi.ai.infrastructure.fastapi.chatbot.FastApiChatRequest;
import com.sashimi.ai.infrastructure.fastapi.chatbot.FastApiChatResponse;
import com.sashimi.ai.infrastructure.fastapi.chatbot.FastApiChatbotClient;
import com.sashimi.chatbot.application.port.CareerChatPort;
import com.sashimi.chatbot.domain.model.ChatMessage;
import com.sashimi.chatbot.domain.model.ChatRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FastAPI(AI 서버)를 호출하는 챗봇 어댑터.
 *
 * <p>질문 분류 · RAG 검색 · 답변 생성은 모두 FastAPI가 처리하고,
 * Spring은 관문 역할(검증·전달)만 한다.
 * 이전 대화(history)는 FastAPI 프롬프트에 그대로 넣을 수 있도록 문자열로 합쳐 전달한다.
 */
@Slf4j
@Component
public class FastApiChatbotAdapter implements CareerChatPort {

    private final FastApiChatbotClient fastApiChatbotClient;

    public FastApiChatbotAdapter(
            FastApiChatbotClient fastApiChatbotClient
    ) {
        this.fastApiChatbotClient = fastApiChatbotClient;
    }

    @Override
    public String generateReply(String message, List<ChatMessage> history) {
        String conversationContext = toConversationContext(history);

        log.info("[Chatbot] FastAPI 답변 요청 - historyCount={}", history.size());

        FastApiChatResponse response = fastApiChatbotClient.chat(
                new FastApiChatRequest(message, conversationContext)
        );

        return response.reply();
    }

    /** 이전 대화를 "사용자: ... / 핏봇: ..." 형태의 문자열로 합친다. 없으면 null. */
    private String toConversationContext(List<ChatMessage> history) {
        if (history == null || history.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (ChatMessage chatMessage : history) {
            String speaker = chatMessage.role() == ChatRole.ASSISTANT ? "핏봇" : "사용자";
            builder.append(speaker)
                    .append(": ")
                    .append(chatMessage.content())
                    .append("\n");
        }
        return builder.toString().strip();
    }
}
