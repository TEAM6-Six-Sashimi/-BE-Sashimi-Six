package com.sashimi.security.jwt;

import com.sashimi.auth.dto.TokenResponseDto;
import com.sashimi.auth.dto.WsTicketResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInMilliseconds;
    }

    private static final String AUTHORITIES_KEY = "auth";
    private static final String ROLE_KEY = "role";
    private static final String USER_ID_KEY = "uid";
    private static final String VERSION_KEY = "ver";
    private static final String PURPOSE_KEY = "purpose";
    public static final String WS_TICKET_PURPOSE = "ws";

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-milliseconds}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long refreshTokenValidityInMilliseconds;

    @Value("${jwt.ws-ticket-validity-in-milliseconds}")
    private long wsTicketValidityInMilliseconds;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenResponseDto generateToken(Authentication authentication, Long userId, long version) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.joining(","));

        long now = new Date().getTime();

        Date accessTokenExpiresIn = new Date(now + accessTokenValidityInMilliseconds);

        String role = authorities.replace("ROLE_", "");

        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(ROLE_KEY, role)
                .claim(USER_ID_KEY, userId)
                .claim(VERSION_KEY, version)
                .issuedAt(new Date(now))
                .expiration(accessTokenExpiresIn)
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(USER_ID_KEY, userId)
                .claim(VERSION_KEY, version)
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshTokenValidityInMilliseconds))
                .signWith(key)
                .compact();

        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public WsTicketResponseDto generateWsTicket(Long userId, long version) {
        long now = new Date().getTime();
        Date expiresIn = new Date(now + wsTicketValidityInMilliseconds);

        String wsTicket = Jwts.builder()
                .claim(USER_ID_KEY, userId)
                .claim(VERSION_KEY, version)
                .claim(PURPOSE_KEY, WS_TICKET_PURPOSE)
                .issuedAt(new Date(now))
                .expiration(expiresIn)
                .signWith(key)
                .compact();

        return new WsTicketResponseDto(wsTicket, wsTicketValidityInMilliseconds / 1000);
    }

    public String extractPurpose(String token) {
        Object purpose = parseClaims(token).get(PURPOSE_KEY);
        return purpose == null ? null : purpose.toString();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT 토큰이 비어 있습니다.");
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ") && bearerToken.length() > 7) {
            return bearerToken.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public Long extractUserId(String token) {
        Object uid = parseClaims(token).get(USER_ID_KEY);
        if (uid == null) return null;
        return ((Number) uid).longValue();
    }

    public Long extractVersion(String token) {
        Object ver = parseClaims(token).get(VERSION_KEY);
        if (ver == null) return null;
        return ((Number) ver).longValue();
    }

    public long getRemainingExpiry(String token) {
        Claims claims = parseClaims(token);
        long expiry = claims.getExpiration().getTime();
        long now = System.currentTimeMillis();
        return Math.max(0, expiry - now);
    }
}
