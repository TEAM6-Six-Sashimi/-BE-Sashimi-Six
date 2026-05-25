package com.sashimi.global.exception;

import lombok.Builder;


import java.time.LocalDateTime;


@Builder
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String errorCode,
        String message,
        String path,
        String traceId
) {
}
