package com.sashimi.token.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "revoked_reason", length = 50)
    private RefreshTokenRevokedReason revokedReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public RefreshToken(Long userId, String token, LocalDateTime expiryDate) {
        LocalDateTime now = LocalDateTime.now();
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
        this.issuedAt = now;
        this.lastUsedAt = now;
        this.createdAt = now;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void updateToken(String token, LocalDateTime expiryDate) {
        LocalDateTime now = LocalDateTime.now();
        this.token = token;
        this.expiryDate = expiryDate;
        this.issuedAt = now;
        this.lastUsedAt = now;
        this.revokedAt = null;
        this.revokedReason = null;
    }

    public void markUsed(LocalDateTime usedAt) {
        this.lastUsedAt = usedAt;
    }
}
