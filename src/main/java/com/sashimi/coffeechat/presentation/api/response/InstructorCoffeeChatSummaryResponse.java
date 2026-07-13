package com.sashimi.coffeechat.presentation.api.response;

import com.sashimi.coffeechat.application.query.CoffeeChatSummaryView;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;

import java.time.LocalDateTime;

public record InstructorCoffeeChatSummaryResponse(
        Long chatId,
        Long studentId,
        Long courseId,
        String courseTitle,
        CoffeeChatStatus status,
        LocalDateTime createdAt,
        LocalDateTime acceptedAt,
        boolean hasUnreadMessages,
        String lastMessagePreview,
        LocalDateTime lastMessageAt
) {
    public static InstructorCoffeeChatSummaryResponse from(CoffeeChatSummaryView view) {
        return new InstructorCoffeeChatSummaryResponse(
                view.chatId(),
                view.studentId(),
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
