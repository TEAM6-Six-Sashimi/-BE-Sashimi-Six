package com.sashimi.coffeechat.application.service;

import com.sashimi.coffeechat.application.usecase.CoffeeChatCommandUseCase;
import com.sashimi.coffeechat.domain.model.CoffeeChat;
import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;
import com.sashimi.coffeechat.domain.repository.CoffeeChatMessageRepository;
import com.sashimi.coffeechat.domain.repository.CoffeeChatRepository;
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
    public void sendMessage(Long chatId, Long senderId, String content) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        CoffeeChatMessage message = chat.sendMessage(senderId, content);
        coffeeChatMessageRepository.save(message);
        coffeeChatRepository.save(chat);
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

    private CoffeeChat getChatForInstructor(Long chatId, Long instructorId) {
        CoffeeChat chat = coffeeChatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COFFEE_CHAT_NOT_FOUND));

        if (!chat.getInstructorId().equals(instructorId)) {
            throw new BusinessException(ErrorCode.COFFEE_CHAT_FORBIDDEN);
        }
        return chat;
    }
}
