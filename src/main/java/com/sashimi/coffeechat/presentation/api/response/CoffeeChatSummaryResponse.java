package com.sashimi.coffeechat.presentation.api.response;

import com.sashimi.coffeechat.application.query.CoffeeChatSummaryView;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;

import java.time.LocalDateTime;

public record CoffeeChatSummaryResponse(
        Long chatId,
        Long instructorId,
        String instructorName,
        Long courseId,
        String courseTitle,
        CoffeeChatStatus status,
        LocalDateTime createdAt,
        LocalDateTime acceptedAt,
        boolean hasUnreadMessages,
        String lastMessagePreview,
        LocalDateTime lastMessageAt
) {
    public static CoffeeChatSummaryResponse from(CoffeeChatSummaryView view) {
        return new CoffeeChatSummaryResponse(
                view.chatId(),
                view.instructorId(),
                view.instructorName(),
                view.courseId(),
                view.courseTitle(),
                view.status(),
                view.createdAt(),
                view.acceptedAt(),
                view.hasUnreadMessages(),
                view.lastMessagePreview(),
                view.lastMessageAt()
        );
    }
}
