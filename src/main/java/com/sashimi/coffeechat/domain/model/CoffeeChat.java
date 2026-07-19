package com.sashimi.coffeechat.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class CoffeeChat {

    private Long id;
    private Long studentId;
    private Long instructorId;
    private Long courseId;
    private CoffeeChatStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime leftAt;

    public static CoffeeChat create(Long studentId, Long instructorId, Long courseId) {
        return CoffeeChat.builder()
                .studentId(studentId)
                .instructorId(instructorId)
                .courseId(courseId)
                .status(CoffeeChatStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public CoffeeChatMessage accept() {
        if (this.status != CoffeeChatStatus.PENDING) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
        }
        this.status = CoffeeChatStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
        return CoffeeChatMessage.createSystemMessage(this.id, this.instructorId, CoffeeChatMessageType.SYSTEM_ACCEPT);
    }

    public CoffeeChatMessage leave() {
        if (this.status != CoffeeChatStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
        }
        this.status = CoffeeChatStatus.LEFT;
        this.leftAt = LocalDateTime.now();
        return CoffeeChatMessage.createSystemMessage(this.id, this.instructorId, CoffeeChatMessageType.SYSTEM_LEAVE);
    }

    public CoffeeChatMessage reject() {
        if (this.status != CoffeeChatStatus.PENDING) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
        }
        this.status = CoffeeChatStatus.REJECTED;
        return CoffeeChatMessage.createSystemMessage(this.id, this.instructorId, CoffeeChatMessageType.SYSTEM_REJECT);
    }

    public CoffeeChatMessage sendMessage(Long senderId, String content) {
        if (!isParticipant(senderId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_MESSAGE_FORBIDDEN);
        }

        boolean isStudent = Objects.equals(senderId, this.studentId);

        if (this.status == CoffeeChatStatus.PENDING && !isStudent) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
        }
        if (this.status == CoffeeChatStatus.LEFT || this.status == CoffeeChatStatus.REJECTED) {
            if (!isStudent) {
                throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
            }
            this.status = CoffeeChatStatus.PENDING;
        }

        return CoffeeChatMessage.create(this.id, senderId, content);
    }

    public boolean isParticipant(Long userId) {
        return Objects.equals(userId, this.studentId) || Objects.equals(userId, this.instructorId);
    }
}
