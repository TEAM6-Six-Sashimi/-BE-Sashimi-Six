package com.sashimi.security.jwt;

import com.sashimi.security.blacklist.TokenBlacklistService;
import com.sashimi.security.session.TokenVersionService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenVersionService tokenVersionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtTokenProvider.resolveToken(request);

        try {
            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                try {
                    Long userId = jwtTokenProvider.extractUserId(accessToken);
                    Long version = jwtTokenProvider.extractVersion(accessToken);
                    if (!tokenBlacklistService.isBlacklisted(accessToken)
                            && tokenVersionService.isValidVersion(userId, version)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    // Redis 장애 시 fail-closed: 인증 거부
                    logger.warn("event=redis_unavailable msg=인증 거부 (fail-closed)");
                }
            }
        } catch (JwtException | IllegalArgumentException | AuthenticationException e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}