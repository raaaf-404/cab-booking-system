package com.cabbooking.security;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Built-in Spring interface to fetch user details

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Early exit if no token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Let the request pass through
            return;
        }

        final String jwt = authHeader.substring(7); // "Bearer ".length() is 7
        final String username;

        try {
            // 2. Extract the username from the token
            username = jwtService.extractUsername(jwt);
        } catch (JwtException e) {
            // If the token is invalid (expired, malformed, etc.), just let the request
            // continue without an authenticated user. Spring will deny access later.
            // A more advanced setup would use an AuthenticationEntryPoint to return a 401.
            logger.warn("JWT processing error: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // 3. If we have a username but the user is not yet authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details from the database (or cache)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Validate the token against the user details
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 5. Create the Authentication object and set it in the SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are not needed for token-based auth
                        userDetails.getAuthorities()
                );
                // Add more details about the request to the auth token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // This is the magic line that authenticates the user for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("User '{}' authenticated successfully. Setting security context.", username);
            }
        }

        // 6. Continue the filter chain for the next filter to process
        filterChain.doFilter(request, response);
    }
}
