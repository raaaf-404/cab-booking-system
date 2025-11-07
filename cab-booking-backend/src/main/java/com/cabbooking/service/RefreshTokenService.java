package com.cabbooking.service;

import com.cabbooking.model.RefreshToken;

import java.util.Optional;


public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByToken(String token);
}
