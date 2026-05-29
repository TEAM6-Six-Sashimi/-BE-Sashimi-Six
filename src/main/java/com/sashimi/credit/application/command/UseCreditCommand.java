package com.sashimi.credit.application.command;

import java.math.BigDecimal;

public record UseCreditCommand(
        Long userId,
        Long amount
) {
}