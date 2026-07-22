package com.sashimi.coffeechat.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CoffeeChatMessage {

    private Long id;
    private Long coffeeChatId;
    private Long senderId;
    private String content;
    private CoffeeChatMessageType messageType;
    private boolean isRead;
    private LocalDateTime createdAt;

    static CoffeeChatMessage create(Long coffeeChatId, Long senderId, String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return CoffeeChatMessage.builder()
                .coffeeChatId(coffeeChatId)
                .senderId(senderId)
                .content(content)
                .messageType(CoffeeChatMessageType.TEXT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    static CoffeeChatMessage createSystemMessage(Long coffeeChatId, Long senderId, CoffeeChatMessageType messageType) {
        return CoffeeChatMessage.builder()
                .coffeeChatId(coffeeChatId)
                .senderId(senderId)
                .content(systemMessageContent(messageType))
                .messageType(messageType)
                .isRead(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static String systemMessageContent(CoffeeChatMessageType messageType) {
        return switch (messageType) {
            case SYSTEM_ACCEPT -> "강사가 요청을 수락했습니다.";
            case SYSTEM_REJECT -> "강사가 요청을 거절했습니다. 다시 메시지를 보내시면 재요청이 가능합니다.";
            case SYSTEM_LEAVE -> "강사가 채팅방을 나갔습니다. 다시 메시지를 보내시면 재요청이 가능합니다.";
            case TEXT -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        };
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
