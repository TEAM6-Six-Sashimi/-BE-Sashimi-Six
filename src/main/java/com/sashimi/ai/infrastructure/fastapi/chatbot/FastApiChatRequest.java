package com.sashimi.ai.infrastructure.fastapi.chatbot;

/**
 * FastAPI 챗봇 요청.
 *
 * @param userMessage         현재 사용자 질문
 * @param conversationContext 이전 대화 요약(없으면 null). Spring이 history를 문자열로 합쳐 전달한다.
 */
public record FastApiChatRequest(
        String userMessage,
        String conversationContext
) {
}
