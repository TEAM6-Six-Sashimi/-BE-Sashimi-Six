package com.sashimi.chatbot.domain.model;

/**
 * 대화 한 줄(발화). 서버는 대화를 저장하지 않고, 요청마다 히스토리를 받아 처리한다(무상태).
 */
public record ChatMessage(ChatRole role, String content) {
}
