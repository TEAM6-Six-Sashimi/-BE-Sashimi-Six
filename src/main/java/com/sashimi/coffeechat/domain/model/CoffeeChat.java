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

    public void accept() {
        if (this.status != CoffeeChatStatus.PENDING) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
        }
        this.status = CoffeeChatStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void leave() {
        if (this.status != CoffeeChatStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
        }
        this.status = CoffeeChatStatus.LEFT;
        this.leftAt = LocalDateTime.now();
    }

    public void validateCanReject() {
        if (this.status != CoffeeChatStatus.PENDING) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
        }
    }

    public CoffeeChatMessage sendMessage(Long senderId, String content) {
        if (this.status != CoffeeChatStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_INVALID_STATUS);
        }
        if (!isParticipant(senderId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_MESSAGE_FORBIDDEN);
        }
        return CoffeeChatMessage.create(this.id, senderId, content);
    }

    public boolean isParticipant(Long userId) {
        return Objects.equals(userId, this.studentId) || Objects.equals(userId, this.instructorId);
    }
}
