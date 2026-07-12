package com.sashimi.coffeechat.domain.repository;

import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;

import java.util.List;
import java.util.Set;

public interface CoffeeChatMessageRepository {

    CoffeeChatMessage save(CoffeeChatMessage message);

    List<CoffeeChatMessage> findAllByCoffeeChatId(Long coffeeChatId, int page, int size);

    Set<Long> findCoffeeChatIdsWithUnreadMessages(List<Long> coffeeChatIds, Long excludeSenderId);

    void markAllAsRead(Long coffeeChatId, Long readerId);
}
