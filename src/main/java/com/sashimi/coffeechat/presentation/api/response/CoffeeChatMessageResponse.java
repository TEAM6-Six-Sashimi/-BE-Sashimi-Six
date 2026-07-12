package com.sashimi.coffeechat.presentation.api.response;

import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;

import java.time.LocalDateTime;

public record CoffeeChatMessageResponse(
        Long messageId,
        Long senderId,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static CoffeeChatMessageResponse from(CoffeeChatMessage message) {
        return new CoffeeChatMessageResponse(
                message.getId(),
                message.getSenderId(),
                message.getContent(),
                message.isRead(),
                message.getCreatedAt()
        );
    }
}
