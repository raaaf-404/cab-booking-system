package com.cabbooking.service.impl;

import com.cabbooking.exception.TokenRefreshException;
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

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found with id: " + userId));

        // Invalidate all old refresh tokens for this user,
        // This means a user can only be logged in on one device at a time.
        // If you want a multi-device login, comment on this line.
        refreshTokenRepository.deleteByUser(user);

        // Create the new token
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);

        RefreshToken refreshToken = new RefreshToken(user, token, expiryDate);
        RefreshToken saveRefreshToken = refreshTokenRepository.save(refreshToken);
        return saveRefreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(
                    token.getToken(),
                    "Refresh token was expired. Please make a new signin request.");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
