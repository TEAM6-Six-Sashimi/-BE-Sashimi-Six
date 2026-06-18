package com.sashimi.credit.application.command;

public record ReadyCreditChargeCommand(
        Long userId,
        Long amount
) {
}