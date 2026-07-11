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
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
