package com.sashimi.security.handler;

import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.global.trace.TraceIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getRequestURI(),
                (String) request.getAttribute(TraceIdFilter.TRACE_ID)
        );

        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}