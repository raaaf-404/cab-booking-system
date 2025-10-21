package com.cabbooking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service // Renamed to Service for better semantic meaning in Spring
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${app.jwt.secret}")
    private String jwtSecretBase64;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey key;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    /**
     * Generates a JWT for an authenticated user.
     * Includes the user's authorities as a custom claim.
     *
     * @param authentication The Spring Security Authentication object.
     * @return A signed JWT string.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        // Extract authorities and convert them to a comma-separated string
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("authorities", authorities) // Add authorities as a custom claim
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the username (subject) from the token.
     * This method will throw exceptions if the token is invalid.
     *
     * @param token The JWT string.
     * @return The username.
     * @throws JwtException if the token cannot be parsed or is invalid.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates the token against UserDetails.
     * Checks if the username in the token matches and if the token is expired.
     *
     * @param token The JWT string.
     * @param userDetails The UserDetails object to validate against.
     * @return true if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        // extractExpiration will throw ExpiredJwtException if expired, which is handled in the calling method
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * A generic function to extract a specific claim from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * The core parsing method. This is where exceptions from JJWT will be thrown.
     */
    private Claims extractAllClaims(String token) {
        // The parser handles signature validation.
        // It will throw SignatureException, MalformedJwtException, etc.
        return jwtParser.parseSignedClaims(token).getPayload();
    }

}
