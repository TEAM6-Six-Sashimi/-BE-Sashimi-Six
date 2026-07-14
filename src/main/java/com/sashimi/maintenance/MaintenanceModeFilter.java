package com.sashimi.maintenance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.exception.ErrorResponse;
import com.sashimi.global.trace.TraceIdFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 점검모드가 켜져 있으면 ROLE_ADMIN과 로그인/헬스체크 등 필수 경로를 제외한 모든
 * 요청을 503 점검 응답으로 차단한다. JwtAuthenticationFilter 다음에 실행되도록
 * 등록되어 있어 SecurityContext에 인증 정보(ROLE_ADMIN 포함)가 이미 채워진 상태에서
 * 판단한다.
 */
@Component
@RequiredArgsConstructor
public class MaintenanceModeFilter extends OncePerRequestFilter {

    private static final List<String> ALWAYS_ALLOWED_PREFIXES = List.of(
            "/auth/login",
            "/auth/reissue",
            "/actuator/health",
            "/maintenance/status"
    );

    private final MaintenanceModeService maintenanceModeService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!maintenanceModeService.isEnabled()
                || isAlwaysAllowed(request.getRequestURI())
                || hasAdminRole()) {
            filterChain.doFilter(request, response);
            return;
        }

        respondWithMaintenance(request, response);
    }

    private boolean isAlwaysAllowed(String uri) {
        return ALWAYS_ALLOWED_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private void respondWithMaintenance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String traceId = (String) request.getAttribute(TraceIdFilter.TRACE_ID);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ErrorCode.SERVICE_UNDER_MAINTENANCE.getStatus())
                .errorCode(ErrorCode.SERVICE_UNDER_MAINTENANCE.getCode())
                .message(maintenanceModeService.getMessage())
                .path(request.getRequestURI())
                .traceId(traceId)
                .build();

        response.setStatus(ErrorCode.SERVICE_UNDER_MAINTENANCE.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
