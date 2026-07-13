package com.sashimi.coverletter.infrastructure.persistence;

import com.sashimi.coverletter.domain.model.CoverLetterQuestion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cover_letters")
public class CoverLetterJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cover_letter_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_key", nullable = false, length = 50)
    private CoverLetterQuestion questionKey;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected CoverLetterJpaEntity() {
    }

    public static CoverLetterJpaEntity create(
            Long userId,
            CoverLetterQuestion questionKey,
            String content
    ) {
        CoverLetterJpaEntity entity = new CoverLetterJpaEntity();
        entity.userId = userId;
        entity.questionKey = questionKey;
        entity.content = content;
        entity.createdAt = LocalDateTime.now();
        return entity;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public CoverLetterQuestion getQuestionKey() {
        return questionKey;
    }

    public String getContent() {
        return content;
    }
}