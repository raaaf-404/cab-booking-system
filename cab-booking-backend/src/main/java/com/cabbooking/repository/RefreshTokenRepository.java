package com.cabbooking.repository;

import com.cabbooking.model.RefreshToken;
import com.cabbooking.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Find a token by its unique string
    Optional<RefreshToken> findByToken(String token);

    // Delete a token by its unique string (for logout)
    // We use @Modifying because this is a DML operation
    @Modifying
    Integer deleteByToken(String token);

    //Delete all tokens for a user
    @Modifying
    Integer deleteByUser(User user);
}
