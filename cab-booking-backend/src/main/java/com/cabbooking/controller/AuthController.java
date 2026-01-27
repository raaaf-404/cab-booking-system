package com.cabbooking.controller;

import com.cabbooking.dto.request.LoginRequest;
import com.cabbooking.dto.request.LogoutRequest;
import com.cabbooking.dto.request.SignupRequest;
import com.cabbooking.dto.request.TokenRefreshRequest;

import com.cabbooking.dto.response.JwtResponse;
import com.cabbooking.dto.response.MessageResponse;
import com.cabbooking.dto.response.TokenRefreshResponse;
import com.cabbooking.dto.response.UserResponse;

import com.cabbooking.security.JwtService; 

import com.cabbooking.service.RefreshTokenService;
import com.cabbooking.service.UserService;
import com.cabbooking.service.impl.UserDetailsServiceImpl;
import com.cabbooking.service.RefreshTokenService;

import com.cabbooking.exception.TokenRefreshException;

import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import com.cabbooking.model.User;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService; // Uncomment when active

    /**
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        userService.registerUser(signUpRequest); 
        return ResponseEntity.ok(new MessageResponse("User registered successfully! Please log in."));
    }
    
    /**
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Authenticate the user, which returns the fully authenticated object
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        // 2. Set the context (essential for Spring Security)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate token using the Authentication object
        String accessToken = jwtService.generateToken(authentication);

        // 4. Get UserDetails and User Entity to get the ID for the refresh token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow(
            () -> new RuntimeException("Error not found after successful authentication")
            );

        // 5. USE REFRESH TOKEN SERVICE: Create and persist the long-lived token
        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken(

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
                
        // 6. Populate JwtResponse with full user data
        return ResponseEntity.ok(new JwtResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles
        ));
    }

    /**
     * POST /api/v1/auth/logout
     * This is the NEW secure logout endpoint. (Requires RefreshTokenService)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequest logoutRequest) {
        // This is the secure server-side invalidation.
        refreshTokenService.deleteByToken(logoutRequest.refreshToken());
        return ResponseEntity.ok(new MessageResponse("Logout successful."));
    }

    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                // 1. Find the token in the database
                .map(refreshTokenService::verifyExpiration) // 2. Verify it has not expired
                .map(refreshToken -> {
                    // 3. If valid, get the user and generate a new access token
                    User user = refreshToken.getUser();
                    
                    // Note: We use the existing Authentication object structure for generation.
                    // This is slightly complex because JwtService only accepts Authentication, 
                    // so we need to fetch UserDetails to manually create an Authentication object 
                    // or, better yet, just update JwtService to accept UserDetails too. 
                    // For now, let's use the efficient approach of fetching UserDetails
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    
                    // Create a temporary Authentication object just for the JwtService
                    Authentication authForToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                        
                    String newAccessToken = jwtService.generateToken(authForToken);
                    
                    return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }
    
}