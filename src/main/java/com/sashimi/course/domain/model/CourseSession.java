package com.sashimi.course.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.UUID;

public class CourseSession {

    private final Long id;
    private final String sessionUid;
    private final Long courseId;
    private final String title;
    private final String videoUrl;
    private final int durationSeconds;
    private final int sessionOrder;
    private final boolean preview;
    private final String attachmentName;
    private final String attachmentUrl;
    private final String attachmentType;
    private final Long attachmentSize;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private CourseSession(Long id, String sessionUid, Long courseId, String title, String videoUrl,
                          int durationSeconds, int sessionOrder, boolean preview,
                          String attachmentName, String attachmentUrl, String attachmentType,
                          Long attachmentSize, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (title == null || title.isBlank()) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        if (sessionUid == null) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        this.id = id;
        this.sessionUid = sessionUid;
        this.courseId = courseId;
        this.title = title;
        this.videoUrl = videoUrl;
        this.durationSeconds = durationSeconds;
        this.sessionOrder = sessionOrder;
        this.preview = preview;
        this.attachmentName = attachmentName;
        this.attachmentUrl = attachmentUrl;
        this.attachmentType = attachmentType;
        this.attachmentSize = attachmentSize;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CourseSession create(String title, String videoUrl, int durationSeconds,
                                       int sessionOrder, boolean preview,
                                       String attachmentName, String attachmentUrl,
                                       String attachmentType, Long attachmentSize) {
        return new CourseSession(null, UUID.randomUUID().toString(), null, title, videoUrl,
                durationSeconds, sessionOrder, preview,
                attachmentName, attachmentUrl, attachmentType, attachmentSize,
                LocalDateTime.now(), null);
    }

    public static CourseSession restore(Long id, String sessionUid, Long courseId, String title,
                                        String videoUrl, int durationSeconds, int sessionOrder,
                                        boolean preview, String attachmentName, String attachmentUrl,
                                        String attachmentType, Long attachmentSize,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new CourseSession(id, sessionUid, courseId, title, videoUrl, durationSeconds, sessionOrder,
                preview, attachmentName, attachmentUrl, attachmentType, attachmentSize, createdAt, updatedAt);
    }

    public Long getId() { return id; }
    public String getSessionUid() { return sessionUid; }
    public Long getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getVideoUrl() { return videoUrl; }
    public int getDurationSeconds() { return durationSeconds; }
    public int getSessionOrder() { return sessionOrder; }
    public boolean isPreview() { return preview; }
    public String getAttachmentName() { return attachmentName; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public String getAttachmentType() { return attachmentType; }
    public Long getAttachmentSize() { return attachmentSize; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
