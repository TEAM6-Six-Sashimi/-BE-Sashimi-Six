package com.sashimi.credit.application.command;

import java.math.BigDecimal;

public record ChargeCreditCommand(
        Long userId,
        Long amount
) {
}