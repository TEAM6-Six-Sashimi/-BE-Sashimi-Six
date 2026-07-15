package com.sashimi.chatbot.presentation.api.response;

/**
 * 챗봇 응답. AI가 생성한 답변 한 줄. (내부 분류 정보는 노출하지 않는다)
 */
public record ChatReplyResponse(String reply) {
}
