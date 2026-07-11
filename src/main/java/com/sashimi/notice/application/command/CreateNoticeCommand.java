package com.sashimi.notice.application.command;

public record CreateNoticeCommand(
        String title,
        String content,
        boolean pinned
) {
}