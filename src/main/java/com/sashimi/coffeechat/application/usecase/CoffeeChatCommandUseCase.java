package com.sashimi.coffeechat.application.usecase;

import com.sashimi.coffeechat.application.command.ApplyCoffeeChatCommand;

public interface CoffeeChatCommandUseCase {

    void apply(ApplyCoffeeChatCommand command);

    void accept(Long chatId, Long instructorId);

    void reject(Long chatId, Long instructorId);

    void leave(Long chatId, Long instructorId);

    void sendMessage(Long chatId, Long senderId, String content);

    void markMessagesAsRead(Long chatId, Long readerId);
}
