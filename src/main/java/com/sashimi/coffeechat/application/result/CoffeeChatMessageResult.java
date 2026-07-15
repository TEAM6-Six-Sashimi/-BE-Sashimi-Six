package com.sashimi.coffeechat.application.result;

import com.sashimi.coffeechat.domain.model.CoffeeChatMessage;

public record CoffeeChatMessageResult(
        CoffeeChatMessage message,
        Long studentId,
        Long instructorId
) {
}
