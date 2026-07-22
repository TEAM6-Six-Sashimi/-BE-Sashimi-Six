package com.sashimi.chatbot.application.usecase;

import com.sashimi.chatbot.domain.model.ChatMessage;

import java.util.List;

/**
 * 챗봇 대화 유스케이스. 컨트롤러는 이 인터페이스에만 의존한다.
 */
public interface ChatbotUseCase {

    /**
     * 현재 질문과 이전 대화를 받아 AI 답변을 반환한다.
     */
    String sendMessage(String message, List<ChatMessage> history);
}
