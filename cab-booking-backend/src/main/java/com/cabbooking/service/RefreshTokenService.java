package com.cabbooking.service;

import com.cabbooking.model.RefreshToken;

import java.util.Optional;


public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(String email);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByToken(String token);
}
