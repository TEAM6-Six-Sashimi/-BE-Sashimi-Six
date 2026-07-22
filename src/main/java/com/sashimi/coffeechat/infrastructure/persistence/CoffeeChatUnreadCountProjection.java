package com.sashimi.coffeechat.infrastructure.persistence;

public interface CoffeeChatUnreadCountProjection {

    Long getCoffeeChatId();

    long getUnreadCount();
}
