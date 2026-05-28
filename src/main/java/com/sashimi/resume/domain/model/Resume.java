package com.sashimi.resume.domain.model;

import java.time.LocalDateTime;

public class Resume {

    private final Long resumeId;
    private final Long userId;
    private final String title;
    private final String content;
    private final boolean defaultResume;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Resume(Long resumeId, Long userId, String title, String content) {
        this(
                resumeId,
                userId,
                title,
                content,
                false,
                LocalDateTime.now(),
                null
        );
    }

    private Resume(
            Long resumeId,
            Long userId,
            String title,
            String content,
            boolean defaultResume,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Resume title is required.");
        }

        this.resumeId = resumeId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.defaultResume = defaultResume;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt;
    }

    public static Resume create(
            Long userId,
            String title,
            String content,
            boolean defaultResume
    ) {
        return new Resume(
                null,
                userId,
                title,
                content,
                defaultResume,
                LocalDateTime.now(),
                null
        );
    }

    public Resume update(
            String title,
            String content,
            Boolean defaultResume
    ) {
        return new Resume(
                this.resumeId,
                this.userId,
                title == null ? this.title : title,
                content == null ? this.content : content,
                defaultResume == null ? this.defaultResume : defaultResume,
                this.createdAt,
                LocalDateTime.now()
        );
    }

    public Resume withId(Long resumeId) {
        return new Resume(
                resumeId,
                this.userId,
                this.title,
                this.content,
                this.defaultResume,
                this.createdAt,
                this.updatedAt
        );
    }

    public Long resumeId() {
        return resumeId;
    }

    public Long userId() {
        return userId;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    public boolean defaultResume() {
        return defaultResume;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }
}