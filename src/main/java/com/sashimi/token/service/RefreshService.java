package com.sashimi.token.service;

import com.sashimi.token.entity.RefreshToken;
import com.sashimi.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.domain.model.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveOrUpdate(User user, String token, LocalDateTime expiryDate) {
        return refreshTokenRepository.findByUserId(user.getId())
                .map(refreshToken -> {
                    refreshToken.updateToken(token, expiryDate);
                    return refreshToken;
                })
                .orElseGet(() -> refreshTokenRepository.save(
                        new RefreshToken(user.getId(), token, expiryDate)
                ));
    }

    @Transactional(readOnly = true)
    public RefreshToken findValidRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        return refreshToken;
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }
}