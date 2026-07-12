package com.sashimi.security.handler;



import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.global.trace.TraceIdFilter;
import com.sashimi.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        boolean concurrentSession = Boolean.TRUE.equals(
                request.getAttribute(JwtAuthenticationFilter.CONCURRENT_SESSION_ATTRIBUTE));
        boolean inactiveUser = Boolean.TRUE.equals(
                request.getAttribute(JwtAuthenticationFilter.INACTIVE_USER_ATTRIBUTE));
        ErrorCode errorCode = concurrentSession
                ? ErrorCode.CONCURRENT_SESSION_DETECTED
                : inactiveUser
                        ? ErrorCode.INACTIVE_USER
                        : ErrorCode.UNAUTHORIZED;

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