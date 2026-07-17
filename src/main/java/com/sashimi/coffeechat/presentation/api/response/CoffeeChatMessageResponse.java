package com.sashimi.coffeechat.presentation.api.response;

import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;

import java.time.LocalDateTime;

public record CoffeeChatMessageResponse(
        String eventType,
        Long messageId,
        Long senderId,
        String content,
        String messageType,
        boolean isRead,
        LocalDateTime createdAt,
        Long lastReadMessageId
) {
    public static CoffeeChatMessageResponse from(CoffeeChatMessage message) {
        return new CoffeeChatMessageResponse(
                "MESSAGE",
                message.getId(),
                message.getSenderId(),
                message.getContent(),
                message.getMessageType().name(),
                message.isRead(),
                message.getCreatedAt(),
                null
        );
    }

    public static CoffeeChatMessageResponse ofRead(Long lastReadMessageId) {
        return new CoffeeChatMessageResponse(
                "READ", null, null, null, null, false, null, lastReadMessageId
        );
    }
}
