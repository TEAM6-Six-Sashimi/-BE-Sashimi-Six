package com.sashimi.coffeechat.application.query;

import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;

import java.time.LocalDateTime;

public record CoffeeChatSummaryView(
        Long chatId,
        Long studentId,
        Long instructorId,
        String instructorName,
        Long courseId,
        String courseTitle,
        CoffeeChatStatus status,
        LocalDateTime createdAt,
        LocalDateTime acceptedAt,
        long unreadMessageCount,
        String instructorProfileImagePath,
        String lastMessagePreview,
        LocalDateTime lastMessageAt
) {}
