package com.sashimi.coffeechat.application.usecase;

import com.sashimi.coffeechat.application.result.CoffeeChatMessageResult;
import com.sashimi.coffeechat.application.result.MarkAsReadResult;

public interface CoffeeChatCommandUseCase {

    void accept(Long chatId, Long instructorId);

    void reject(Long chatId, Long instructorId);

    void leave(Long chatId, Long instructorId);

    CoffeeChatMessageResult sendMessage(Long chatId, Long senderId, String content);

    MarkAsReadResult markMessagesAsRead(Long chatId, Long readerId);

    int backfillMissingChatRooms();
}
