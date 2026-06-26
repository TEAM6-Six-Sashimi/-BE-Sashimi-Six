package com.sashimi.verification.infrastructure.outbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailOutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String toEmail;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    @Column(nullable = false)
    private int retryCount;

    public static EmailOutboxJpaEntity create(String toEmail, String subject, String content) {
        EmailOutboxJpaEntity entity = new EmailOutboxJpaEntity();
        entity.toEmail = toEmail;
        entity.subject = subject;
        entity.content = content;
        entity.status = OutboxStatus.PENDING;
        entity.createdAt = LocalDateTime.now();
        entity.retryCount = 0;
        return entity;
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markFailed(int maxRetry) {
        this.retryCount++;
        if (this.retryCount >= maxRetry) {
            this.status = OutboxStatus.FAILED;
        }
        // maxRetry 미만이면 PENDING 유지 → 다음 스케줄러 실행 시 재시도
    }
}
