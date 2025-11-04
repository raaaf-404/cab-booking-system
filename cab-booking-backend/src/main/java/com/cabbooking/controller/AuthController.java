package com.cabbooking.controller;

import com.cabbooking.dto.request.LoginRequest;
import com.cabbooking.dto.request.LogoutRequest;
import com.cabbooking.dto.request.SignupRequest;
import com.cabbooking.dto.request.TokenRefreshRequest;
import com.cabbooking.dto.response.JwtResponse;
import com.cabbooking.dto.response.MessageResponse;
import com.cabbooking.dto.response.TokenRefreshResponse;
import com.cabbooking.dto.response.UserResponse; // Keep this import
import com.cabbooking.security.JwtService; // Your existing JWT service
import com.cabbooking.service.RefreshTokenService; // You will need to create this service
import com.cabbooking.service.UserService; // Your existing UserService
import com.cabbooking.service.impl.UserDetailsServiceImpl; // Your existing service
import org.springframework.security.core.userdetails.UserDetails; // Correct import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final JwtService jwtService; // Your existing JwtService
    private final UserDetailsServiceImpl userDetailsService; // Your UserDetailsService

    // You will need to create this service to manage refresh tokens in your DB
    // private final RefreshTokenService refreshTokenService;

    /**
     * POST /api/v1/auth/register
     * This logic is MOVED from UserController.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // We use your existing UserService, which already handles password encoding
        // and UserAlreadyExistsException.
        UserResponse userResponse = userService.registerUser(signUpRequest);

        // Per best practices, we return a simple success message.
        // The client should then be directed to the login page.
        return ResponseEntity.ok(new MessageResponse("User registered successfully! Please log in."));
    }

    /**
     * POST /api/v1/auth/login
     * This is the NEW login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // We use your existing UserDetailsServiceImpl to get UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());

        String accessToken = jwtService.generateToken(userDetails);

        // --- This is the new part ---
        // You need to implement RefreshTokenService to create and store this.
        // String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        // For now, let's just use the access token as a placeholder until you build RefreshTokenService
        String refreshToken = accessToken; // <-- TODO: Replace with real RefreshTokenService logic

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // We return our new JwtResponse DTO
        // You'll need to fetch the ID and email, perhaps from your User object
        return ResponseEntity.ok(new JwtResponse(
                accessToken,
                refreshToken,
                null, // TODO: Get User ID
                userDetails.getUsername(),
                null, // TODO: Get User Email
                roles
        ));
    }

    /**
     * POST /api/v1/auth/refresh
     * This is the NEW refresh endpoint. (Requires RefreshTokenService)
     */
    // @PostMapping("/refresh")
    // public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
    //     return refreshTokenService.findByToken(request.refreshToken())
    //             .map(refreshTokenService::verifyExpiration)
    //             .map(refreshToken -> {
    //                 UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());
    //                 String newAccessToken = jwtService.generateToken(userDetails);
    //                 return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, request.refreshToken()));
    //             })
    //             .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    // }

    /**
     * POST /api/v1/auth/logout
     * This is the NEW secure logout endpoint. (Requires RefreshTokenService)
     */
    // @PostMapping("/logout")
    // public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequest logoutRequest) {
    //     refreshTokenService.deleteByToken(logoutRequest.refreshToken());
    //     return ResponseEntity.ok(new MessageResponse("Logout successful."));
    // }
}