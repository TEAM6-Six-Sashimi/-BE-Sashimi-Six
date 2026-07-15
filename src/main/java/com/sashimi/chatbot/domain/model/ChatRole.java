package com.sashimi.chatbot.domain.model;

/**
 * 대화 메시지의 발화 주체. 프론트 계약의 role("user"/"assistant")과 대응된다.
 */
public enum ChatRole {
    USER,
    ASSISTANT
}
