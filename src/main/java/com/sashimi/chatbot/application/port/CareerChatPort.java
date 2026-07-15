package com.sashimi.chatbot.application.port;

import com.sashimi.chatbot.domain.model.ChatMessage;

import java.util.List;

/**
 * 진로상담 챗봇 답변 생성 포트.
 *
 * <p>이 인터페이스가 교체 지점이다.
 * 전략 A(현재): Gemini를 직접 호출하는 어댑터가 구현한다.
 * 이후(FastAPI): 분류·RAG·답변을 처리하는 FastAPI를 호출하는 어댑터로 교체할 수 있다.
 */
public interface CareerChatPort {

    /**
     * 현재 질문과 이전 대화를 받아 답변을 생성한다.
     *
     * @param message 현재 사용자 질문
     * @param history 이전 대화(시간순, 없으면 빈 리스트)
     * @return AI가 생성한 답변 텍스트
     */
    String generateReply(String message, List<ChatMessage> history);
}
