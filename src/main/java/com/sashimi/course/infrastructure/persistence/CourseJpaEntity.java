package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.domain.model.RejectReasonCategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class CourseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private CourseDifficulty difficulty;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "total_duration", nullable = false)
    private int totalDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseStatus status;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason_category", length = 50)
    private RejectReasonCategory rejectReasonCategory;

    @Column(name = "reject_detail", length = 500)
    private String rejectDetail;

    @Column(name = "rating_avg", nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingAvg;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "student_count", nullable = false)
    private int studentCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sessionOrder ASC")
    private List<CourseSessionJpaEntity> sessions = new ArrayList<>();

    protected CourseJpaEntity() {}

    public CourseJpaEntity(Long instructorId, Long categoryId,
                           String title, String description,
                           Long price, CourseDifficulty difficulty, String thumbnail,
                           int totalDuration, CourseStatus status, String rejectReason,
                           BigDecimal ratingAvg, int reviewCount, int studentCount,
                           LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime approvedAt) {
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
    }

    public void update(Long categoryId, String title, String description, Long price,
                       CourseDifficulty difficulty, String thumbnail, int totalDuration,
                       CourseStatus status, LocalDateTime updatedAt) {
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.difficulty = difficulty;
        this.thumbnail = thumbnail;
        this.totalDuration = totalDuration;
        this.status = status;
        this.rejectReason = null;
        this.rejectReasonCategory = null;
        this.rejectDetail = null;
        this.updatedAt = updatedAt;
    }

    public void approve(LocalDateTime approvedAt, LocalDateTime updatedAt) {
        this.status = CourseStatus.APPROVED;
        this.rejectReason = null;
        this.rejectReasonCategory = null;
        this.rejectDetail = null;
        this.approvedAt = approvedAt;
        this.updatedAt = updatedAt;
    }

    public void reject(String reason, RejectReasonCategory category, String detail, LocalDateTime updatedAt) {
        this.status = CourseStatus.REJECTED;
        this.rejectReason = reason;
        this.rejectReasonCategory = category;
        this.rejectDetail = detail;
        this.updatedAt = updatedAt;
    }

    public void markArchived(boolean archived) {
        this.archived = archived;
    }

    public void clearSessions() {
        this.sessions.clear();
    }

    public void addSession(CourseSessionJpaEntity session) {
        sessions.add(session);
        session.setCourse(this);
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
    public RejectReasonCategory getRejectReasonCategory() { return rejectReasonCategory; }
    public String getRejectDetail() { return rejectDetail; }
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public int getReviewCount() { return reviewCount; }
    public int getStudentCount() { return studentCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public boolean isArchived() { return archived; }
    public List<CourseSessionJpaEntity> getSessions() { return sessions; }
}