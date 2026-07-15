package com.sashimi.coffeechat.domain.repository;

import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;

import java.util.List;
import java.util.Map;

public interface CoffeeChatMessageRepository {

    CoffeeChatMessage save(CoffeeChatMessage message);

    List<CoffeeChatMessage> findAllByCoffeeChatId(Long coffeeChatId, int page, int size);

    Map<Long, Long> countUnreadMessagesByCoffeeChatIds(List<Long> coffeeChatIds, Long excludeSenderId);

    void markAllAsRead(Long coffeeChatId, Long readerId);

    Map<Long, CoffeeChatMessage> findLatestMessagesByCoffeeChatIds(List<Long> coffeeChatIds);
}
