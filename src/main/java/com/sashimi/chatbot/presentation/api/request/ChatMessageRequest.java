package com.sashimi.chatbot.presentation.api.request;

import com.sashimi.chatbot.domain.model.ChatMessage;
import com.sashimi.chatbot.domain.model.ChatRole;

import java.util.List;

/**
 * 챗봇 대화 요청. 서버가 대화를 저장하지 않으므로 프론트가 이전 대화 전체를 함께 보낸다.
 *
 * @param message 현재 사용자 질문
 * @param history 이전 대화(시간순: 오래된 것 → 최신). 첫 질문이면 빈 배열
 */
public record ChatMessageRequest(String message, List<MessageDto> history) {

    /**
     * @param role    "user" 또는 "assistant"(대소문자 무관)
     * @param content 메시지 본문
     */
    public record MessageDto(String role, String content) {
    }

    public List<ChatMessage> toHistory() {
        if (history == null) {
            return List.of();
        }
        return history.stream()
                .map(dto -> new ChatMessage(resolveRole(dto.role()), dto.content()))
                .toList();
    }

    private static ChatRole resolveRole(String role) {
        if (role == null) {
            return ChatRole.USER;
        }
        String normalized = role.trim().toLowerCase();
        if (normalized.equals("assistant") || normalized.equals("model")) {
            return ChatRole.ASSISTANT;
        }
        return ChatRole.USER;
    }
}
