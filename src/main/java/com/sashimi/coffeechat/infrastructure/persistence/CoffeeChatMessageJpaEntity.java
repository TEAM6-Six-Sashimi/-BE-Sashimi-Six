package com.sashimi.coffeechat.infrastructure.persistence;

import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "coffee_chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoffeeChatMessageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "coffee_chat_id", nullable = false)
    private Long coffeeChatId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public CoffeeChatMessageJpaEntity(Long id, Long coffeeChatId, Long senderId, String content,
                                       boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.coffeeChatId = coffeeChatId;
        this.senderId = senderId;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static CoffeeChatMessageJpaEntity from(CoffeeChatMessage domain) {
        return CoffeeChatMessageJpaEntity.builder()
                .id(domain.getId())
                .coffeeChatId(domain.getCoffeeChatId())
                .senderId(domain.getSenderId())
                .content(domain.getContent())
                .isRead(domain.isRead())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public CoffeeChatMessage toDomain() {
        return CoffeeChatMessage.builder()
                .id(id)
                .coffeeChatId(coffeeChatId)
                .senderId(senderId)
                .content(content)
                .isRead(isRead)
                .createdAt(createdAt)
                .build();
    }
}
