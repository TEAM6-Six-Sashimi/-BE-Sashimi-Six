package com.sashimi.coffeechat.application.command;

public record ApplyCoffeeChatCommand(
        Long studentId,
        Long instructorId,
        Long courseId
) {}
