package com.sashimi.coffeechat.application.result;

public record MarkAsReadResult(
        Long lastReadMessageId,
        Long notifyUserId
) {
}
