package com.sashimi.ai.infrastructure.fastapi.chatbot;

/**
 * FastAPI 챗봇 응답.
 *
 * @param reply AI가 생성한 자연어 답변
 */
public record FastApiChatResponse(
        String reply
) {
}
