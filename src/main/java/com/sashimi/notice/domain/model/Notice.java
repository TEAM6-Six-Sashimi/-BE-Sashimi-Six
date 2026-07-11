package com.sashimi.notice.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDateTime;

public class Notice {

    private final Long id;
    private final String title;
    private final String content;
    private final boolean pinned;
    private final LocalDateTime createdAt;

    private Notice(
            Long id,
            String title,
            String content,
            boolean pinned,
            LocalDateTime createdAt
    ) {
        validateTitle(title);
        validateContent(content);

        this.id = id;
        this.title = title.trim();
        this.content = content.trim();
        this.pinned = pinned;
        this.createdAt = createdAt;
    }

    public static Notice create(
            String title,
            String content,
            boolean pinned
    ) {
        return new Notice(
                null,
                title,
                content,
                pinned,
                LocalDateTime.now()
        );
    }

    public static Notice restore(
            Long id,
            String title,
            String content,
            boolean pinned,
            LocalDateTime createdAt
    ) {
        return new Notice(
                id,
                title,
                content,
                pinned,
                createdAt
        );
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.NOTICE_TITLE_REQUIRED);
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.NOTICE_CONTENT_REQUIRED);
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isPinned() {
        return pinned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}