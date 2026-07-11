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
        if (!Objects.equals(senderId, this.studentId) && !Objects.equals(senderId, this.instructorId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_MESSAGE_FORBIDDEN);
        }
        return CoffeeChatMessage.create(this.id, senderId, content);
    }
}
