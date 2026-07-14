package com.sashimi.coffeechat.infrastructure.websocket;

import com.sashimi.security.blacklist.TokenBlacklistService;
import com.sashimi.security.jwt.JwtTokenProvider;
import com.sashimi.security.session.TokenVersionService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenVersionService tokenVersionService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Long userId = authenticate(accessor.getFirstNativeHeader("Authorization"));
            accessor.setUser(new StompPrincipal(String.valueOf(userId)));
        }

        return message;
    }

    private Long authenticate(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ") || bearerToken.length() <= 7) {
            throw new AuthenticationCredentialsNotFoundException("웹소켓 연결에 유효한 인증 정보가 없습니다.");
        }

        String accessToken = bearerToken.substring(7);

        try {
            if (!jwtTokenProvider.validateToken(accessToken)) {
                throw new AuthenticationCredentialsNotFoundException("유효하지 않은 토큰입니다.");
            }

            Long userId = jwtTokenProvider.extractUserId(accessToken);
            Long version = jwtTokenProvider.extractVersion(accessToken);

            if (tokenBlacklistService.isBlacklisted(accessToken) || !tokenVersionService.isValidVersion(userId, version)) {
                throw new AuthenticationCredentialsNotFoundException("만료되었거나 무효화된 토큰입니다.");
            }

            return userId;
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthenticationCredentialsNotFoundException("토큰 검증에 실패했습니다.");
        }
    }
}
