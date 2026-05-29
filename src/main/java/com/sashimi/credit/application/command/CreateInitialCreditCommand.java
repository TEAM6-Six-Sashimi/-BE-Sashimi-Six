package com.sashimi.credit.application.command;

import java.math.BigDecimal;

public record CreateInitialCreditCommand(
        Long userId,
        Long initialBalance
) {
}