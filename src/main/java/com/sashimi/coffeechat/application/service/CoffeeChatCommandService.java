package com.sashimi.coffeechat.application.service;

import com.sashimi.coffeechat.application.event.EnrollmentCreatedCoffeeChatHandler;
import com.sashimi.coffeechat.application.result.CoffeeChatMessageResult;
import com.sashimi.coffeechat.application.usecase.CoffeeChatCommandUseCase;
import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;
import com.sashimi.coffeechat.domain.repository.CoffeeChatMessageRepository;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.enrollment.application.port.PaidEnrollment;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CoffeeChatCommandService implements CoffeeChatCommandUseCase {

    private final CoffeeChatRepository coffeeChatRepository;
    private final CoffeeChatMessageRepository coffeeChatMessageRepository;
    private final EnrollmentPort enrollmentPort;
    private final EnrollmentCreatedCoffeeChatHandler enrollmentCreatedCoffeeChatHandler;

    @Override
    public void accept(Long chatId, Long instructorId) {
        CoffeeChat chat = getChatForInstructor(chatId, instructorId);
        chat.accept();
        coffeeChatRepository.save(chat);
    }

    @Override
    public void reject(Long chatId, Long instructorId) {
        CoffeeChat chat = getChatForInstructor(chatId, instructorId);
        chat.reject();
        coffeeChatRepository.save(chat);
    }

    @Override
    public void leave(Long chatId, Long instructorId) {
        CoffeeChat chat = getChatForInstructor(chatId, instructorId);
        chat.leave();
        coffeeChatRepository.save(chat);
    }

    @Override
    public CoffeeChatMessageResult sendMessage(Long chatId, Long senderId, String content) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        CoffeeChatMessage message = chat.sendMessage(senderId, content);
        CoffeeChatMessage savedMessage = coffeeChatMessageRepository.save(message);
        coffeeChatRepository.save(chat);

        return new CoffeeChatMessageResult(savedMessage, chat.getStudentId(), chat.getInstructorId());
    }

    @Override
    public void markMessagesAsRead(Long chatId, Long readerId) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        if (!chat.isParticipant(readerId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_FORBIDDEN);
        }

        coffeeChatMessageRepository.markAllAsRead(chatId, readerId);
    }

    @Override
    public int backfillMissingChatRooms() {
        int createdCount = 0;
        for (PaidEnrollment enrollment : enrollmentPort.getAllPaidEnrollments()) {
            if (enrollmentCreatedCoffeeChatHandler.createIfNotExists(enrollment.userId(), enrollment.courseId())) {
                createdCount++;
            }
        }
        return createdCount;
    }

    private CoffeeChat getChatForInstructor(Long chatId, Long instructorId) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        if (!chat.getInstructorId().equals(instructorId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_FORBIDDEN);
        }
        return chat;
    }
}
