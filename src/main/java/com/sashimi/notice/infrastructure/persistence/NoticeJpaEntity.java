package com.sashimi.notice.infrastructure.persistence;

import com.sashimi.notice.domain.model.Notice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "pinned", nullable = false)
    private boolean pinned;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private NoticeJpaEntity(
            Long id,
            String title,
            String content,
            boolean pinned,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.createdAt = createdAt;
    }

    public static NoticeJpaEntity from(Notice notice) {
        return new NoticeJpaEntity(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.isPinned(),
                notice.getCreatedAt()
        );
    }

    public Notice toDomain() {
        return Notice.restore(
                id,
                title,
                content,
                pinned,
                createdAt
        );
    }
}