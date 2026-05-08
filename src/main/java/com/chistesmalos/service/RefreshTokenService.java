package com.chistesmalos.service;

import com.chistesmalos.entity.RefreshToken;
import com.chistesmalos.entity.User;
import com.chistesmalos.exception.AuthenticationException;
import com.chistesmalos.repository.RefreshTokenRepository;
import com.chistesmalos.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Servicio de negocio para gestionar refresh tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        LocalDateTime expiryDate = LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpirationTime()));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(jwtTokenProvider.generateRefreshToken(user.getUsername()))
                .expiryDate(expiryDate)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthenticationException("Refresh token inválido o expirado"));

        if (token.isRevoked()) {
            throw new AuthenticationException("Refresh token revocado");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("Refresh token expirado");
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthenticationException("Refresh token inválido");
        }

        return token;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken refreshToken) {
        String newToken = jwtTokenProvider.generateRefreshToken(refreshToken.getUser().getUsername());
        refreshToken.setToken(newToken);
        refreshToken.setExpiryDate(LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpirationTime())));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeToken(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            log.info("Refresh token revocado para usuario {}", token.getUser().getUsername());
        });
    }
}
