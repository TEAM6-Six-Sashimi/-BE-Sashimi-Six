package com.sashimi.coffeechat.application.usecase;

public interface CoffeeChatCommandUseCase {

    void accept(Long chatId, Long instructorId);

    void reject(Long chatId, Long instructorId);

    void leave(Long chatId, Long instructorId);

    void sendMessage(Long chatId, Long senderId, String content);

    void markMessagesAsRead(Long chatId, Long readerId);
}
