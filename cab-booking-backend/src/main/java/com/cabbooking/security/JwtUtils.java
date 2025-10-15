package com.cabbooking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    // Best practice: Use a logger for better debugging.
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecretBase64;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey key;
    private JwtParser jwtParser; // WHY: Reusable, thread-safe parser instance.

    @PostConstruct
    public void init() {
        // WHY: Using JJWT's Decoders is a bit cleaner than java.util.Base64
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);

        // WHY: Keys.hmacShaKeyFor() securely creates a SecretKey, inferring the algorithm
        // (e.g., HS256, HS384, HS512) from the key length. Ensure your secret is strong!
        this.key = Keys.hmacShaKeyFor(keyBytes);

        // WHY: Build the parser once and reuse it. This is the modern, recommended approach.
        // The verifyWith() method replaces the deprecated setSigningKey().
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    /**
     * Generates a token for a given username with no extra claims.
     */
    public String generateToken(String username) {
        return generateToken(username, Collections.emptyMap());
    }

    /**
     * Generates a token for a given username with additional claims.
     */
    public String generateToken(String username, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key) // The signWith(SecretKey) method is the correct modern usage.
                .compact();
    }

    /**
     * Validates the token's signature and expiration, logging specific errors.
     *
     * @return true if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token) {
        try {
            // The parser automatically handles signature and expiration validation.
            // If it doesn't throw an exception, the token is valid.
            jwtParser.parse(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty or null: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts the username (subject) from the token.
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * A generic function to extract a specific claim from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // WHY: This private helper method centralizes the token parsing logic (DRY principle).
    // All other public methods now rely on this single source of truth for parsing.
    private Claims extractAllClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}