package com.sashimi.token.service;

import com.sashimi.token.entity.RefreshToken;
import com.sashimi.token.repository.RefreshTokenRepository;
import com.sashimi.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveOrUpdate(User user, String token, LocalDateTime expiryDate) {
        return refreshTokenRepository.findByUser(user)
                .map(refreshToken -> {
                    refreshToken.updateToken(token, expiryDate);
                    return refreshToken;
                })
                .orElseGet(() -> refreshTokenRepository.save(
                        new RefreshToken(user, token, expiryDate)
                ));
    }

    @Transactional(readOnly = true)
    public RefreshToken findValidRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 refresh token입니다."));

        if (refreshToken.isExpired()) {
            throw new IllegalArgumentException("만료된 refresh token입니다.");
        }

        return refreshToken;
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}