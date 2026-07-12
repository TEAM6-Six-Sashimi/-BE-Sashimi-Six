package com.sashimi.coffeechat.infrastructure.persistence;

import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coffee_chats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoffeeChatJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coffee_chat_id")
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CoffeeChatStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Builder
    public CoffeeChatJpaEntity(Long id, Long studentId, Long instructorId, Long courseId,
                                CoffeeChatStatus status, LocalDateTime createdAt,
                                LocalDateTime acceptedAt, LocalDateTime leftAt) {
        this.id = id;
        this.studentId = studentId;
        this.instructorId = instructorId;
        this.courseId = courseId;
        this.status = status;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
        this.leftAt = leftAt;
    }

    public static CoffeeChatJpaEntity from(CoffeeChat domain) {
        return CoffeeChatJpaEntity.builder()
                .id(domain.getId())
                .studentId(domain.getStudentId())
                .instructorId(domain.getInstructorId())
                .courseId(domain.getCourseId())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .acceptedAt(domain.getAcceptedAt())
                .leftAt(domain.getLeftAt())
                .build();
    }

    public CoffeeChat toDomain() {
        return CoffeeChat.builder()
                .id(id)
                .studentId(studentId)
                .instructorId(instructorId)
                .courseId(courseId)
                .status(status)
                .createdAt(createdAt)
                .acceptedAt(acceptedAt)
                .leftAt(leftAt)
                .build();
    }
}
