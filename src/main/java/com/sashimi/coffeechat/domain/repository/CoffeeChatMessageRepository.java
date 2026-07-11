package com.sashimi.coffeechat.domain.repository;

import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;

import java.util.List;

public interface CoffeeChatMessageRepository {

    CoffeeChatMessage save(CoffeeChatMessage message);

    List<CoffeeChatMessage> findAllByCoffeeChatId(Long coffeeChatId, int page, int size);
}
