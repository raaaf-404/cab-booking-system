package com.cabbooking.service.impl;

import com.cabbooking.exception.TokenRefreshException;
import com.cabbooking.exception.ResourceNotFoundException;

import com.cabbooking.model.RefreshToken;
import com.cabbooking.model.User;

import com.cabbooking.service.RefreshTokenService;

import com.cabbooking.repository.RefreshTokenRepository;
import com.cabbooking.repository.UserRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.Instant;
import java.util.UUID;


/**
 * Implementation of RefreshTokenService handling the lifecycle of long-lived tokens.
 * Provides security by invalidating old sessions and verifying token expiration.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.jwt.refresh-token-duration-ms}")
    private Long refreshTokenDurationMs;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Creates a new refresh token for a user identified by email.
     * Implementation follows a "Single Session" policy by clearing old tokens.
     */
    @Override
    @Transactional
    public RefreshToken createRefreshToken(String email) {
        // 1. Fetch user by email to align with our Security Principal logic
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // 2. Security Policy: Invalidate old tokens (Single active session per user)
        refreshTokenRepository.deleteByUser(user);

        // 3. Construct the new token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token expired. Please sign in again.");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
