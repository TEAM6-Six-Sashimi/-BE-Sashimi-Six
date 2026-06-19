package com.sashimi.course.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Course {

    private final Long id;
    private final Long instructorId;
    private final Long categoryId;
    private final String title;
    private final String description;
    private final Long price;
    private final CourseDifficulty difficulty;
    private final String thumbnail;
    private final int totalDuration;
    private final CourseStatus status;
    private final String rejectReason;
    private final BigDecimal ratingAvg;
    private final int reviewCount;
    private final int studentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime approvedAt;
    private final List<CourseSession> sessions;

    private Course(Long id, Long instructorId, Long categoryId, String title, String description,
                   Long price, CourseDifficulty difficulty, String thumbnail, int totalDuration,
                   CourseStatus status, String rejectReason, BigDecimal ratingAvg,
                   int reviewCount, int studentCount, LocalDateTime createdAt,
                   LocalDateTime updatedAt, LocalDateTime approvedAt, List<CourseSession> sessions) {
        if (instructorId == null) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        if (categoryId == null) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        if (title == null || title.isBlank()) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        if (price == null || price < 0) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        if (difficulty == null) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        if (status == null) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);

        this.id = id;
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.difficulty = difficulty;
        this.thumbnail = thumbnail;
        this.totalDuration = totalDuration;
        this.status = status;
        this.rejectReason = rejectReason;
        this.ratingAvg = ratingAvg;
        this.reviewCount = reviewCount;
        this.studentCount = studentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.approvedAt = approvedAt;
        this.sessions = sessions != null ? sessions : List.of();
    }
    public static Course create(Long instructorId, Long categoryId,
                                String title, String description, Long price,
                                CourseDifficulty difficulty, String thumbnail,
                                CourseStatus initialStatus, List<CourseSession> sessions) {
        validateWritableStatus(initialStatus);
        int totalDuration = sessions == null ? 0 : sessions.stream().mapToInt(CourseSession::getDurationSeconds).sum();
        return new Course(null, instructorId, categoryId, title, description, price,
                difficulty, thumbnail, totalDuration, initialStatus, null,
                BigDecimal.ZERO, 0, 0, LocalDateTime.now(), null, null, sessions);
    }

    public static Course restore(Long id, Long instructorId, Long categoryId,
                                 String title, String description, Long price,
                                 CourseDifficulty difficulty, String thumbnail, int totalDuration,
                                 CourseStatus status, String rejectReason, BigDecimal ratingAvg,
                                 int reviewCount, int studentCount, LocalDateTime createdAt,
                                 LocalDateTime updatedAt, LocalDateTime approvedAt,
                                 List<CourseSession> sessions) {
        return new Course(id, instructorId, categoryId, title, description, price,
                difficulty, thumbnail, totalDuration, status, rejectReason, ratingAvg,
                reviewCount, studentCount, createdAt, updatedAt, approvedAt, sessions);
    }

    public Course update(Long categoryId, String title, String description, Long price,
                         CourseDifficulty difficulty, String thumbnail, CourseStatus targetStatus,
                         List<CourseSession> sessions) {
        if (!canModify()) throw new BusinessException(ErrorCode.COURSE_NOT_MODIFIABLE);
        validateWritableStatus(targetStatus);
        int totalDuration = sessions == null ? 0 : sessions.stream().mapToInt(CourseSession::getDurationSeconds).sum();
        return new Course(this.id, this.instructorId, categoryId, title, description,
                price, difficulty, thumbnail, totalDuration, targetStatus, null,
                this.ratingAvg, this.reviewCount, this.studentCount, this.createdAt,
                LocalDateTime.now(), this.approvedAt, sessions);
    }

    public Course approve() {
        if (this.status != CourseStatus.PENDING) throw new BusinessException(ErrorCode.COURSE_NOT_PENDING);
        return new Course(id, instructorId, categoryId, title, description, price,
                difficulty, thumbnail, totalDuration, CourseStatus.APPROVED, null,
                ratingAvg, reviewCount, studentCount, createdAt, LocalDateTime.now(),
                LocalDateTime.now(), sessions);
    }

    public Course reject(String reason) {
        if (this.status != CourseStatus.PENDING) throw new BusinessException(ErrorCode.COURSE_NOT_PENDING);
        return new Course(id, instructorId, categoryId, title, description, price,
                difficulty, thumbnail, totalDuration, CourseStatus.REJECTED, reason,
                ratingAvg, reviewCount, studentCount, createdAt, LocalDateTime.now(),
                null, sessions);
    }

    /** 승인 강의의 공개 기간 만료 시 비공개(CLOSED) 처리. 승인일은 보존한다. */
    public Course close() {
        if (this.status != CourseStatus.APPROVED) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        return new Course(id, instructorId, categoryId, title, description, price,
                difficulty, thumbnail, totalDuration, CourseStatus.CLOSED, rejectReason,
                ratingAvg, reviewCount, studentCount, createdAt, LocalDateTime.now(),
                approvedAt, sessions);
    }

    private static void validateWritableStatus(CourseStatus status) {
        if (status != CourseStatus.DRAFT && status != CourseStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public boolean canModify() {
        return status == CourseStatus.DRAFT || status == CourseStatus.PENDING || status == CourseStatus.REJECTED;
    }

    public boolean canDelete() {
        return status == CourseStatus.DRAFT || status == CourseStatus.PENDING || status == CourseStatus.REJECTED;
    }

    public Long getId() { return id; }
    public Long getInstructorId() { return instructorId; }
    public Long getCategoryId() { return categoryId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Long getPrice() { return price; }
    public CourseDifficulty getDifficulty() { return difficulty; }
    public String getThumbnail() { return thumbnail; }
    public int getTotalDuration() { return totalDuration; }
    public CourseStatus getStatus() { return status; }
    public String getRejectReason() { return rejectReason; }
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public int getReviewCount() { return reviewCount; }
    public int getStudentCount() { return studentCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public List<CourseSession> getSessions() { return sessions; }
}
