package com.sashimi.course.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_sessions")
public class CourseSessionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(name = "session_uid", nullable = false, length = 36)
    private String sessionUid;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    @Column(name = "session_order", nullable = false)
    private int sessionOrder;

    @Column(name = "is_preview", nullable = false)
    private boolean preview;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "attachment_type")
    private String attachmentType;

    @Column(name = "attachment_size")
    private Long attachmentSize;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseJpaEntity course;

    protected CourseSessionJpaEntity() {}

    public CourseSessionJpaEntity(String sessionUid, String title, String videoUrl, int durationSeconds,
                                   int sessionOrder, boolean preview, String attachmentName,
                                   String attachmentUrl, String attachmentType, Long attachmentSize,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.sessionUid = sessionUid;
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

    void setCourse(CourseJpaEntity course) { this.course = course; }

    public Long getId() { return id; }
    public String getSessionUid() { return sessionUid; }
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
    public CourseJpaEntity getCourse() { return course; }
}
