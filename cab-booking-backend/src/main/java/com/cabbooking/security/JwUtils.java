package com.cabbooking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecretBase64;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        // Key from base64 encoded secret
        byte[] secretBytes = java.util.Base64.getDecoder().decode(jwtSecretBase64);
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(String username, Map<String, Object> extraClaims) {
        long now = System.currentTimeMillis();
        JwtBuilder b = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256);
        if (extraClaims != null) {
            b.addClaims(extraClaims);
        }
        return b.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Log the error at debug or info
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }
}