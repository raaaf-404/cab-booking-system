package com.cabbooking.controller;

import com.cabbooking.dto.request.LoginRequest;
import com.cabbooking.dto.request.LogoutRequest;
import com.cabbooking.dto.request.PassengerSignupRequest;
import com.cabbooking.dto.request.DriverSignupRequest;
import com.cabbooking.dto.request.TokenRefreshRequest;

import com.cabbooking.dto.response.JwtResponse;
import com.cabbooking.dto.response.AuthResponse;
import com.cabbooking.dto.response.MessageResponse;
import com.cabbooking.dto.response.TokenRefreshResponse;

import com.cabbooking.security.JwtService;
import com.cabbooking.security.UserPrincipal;

import com.cabbooking.service.AuthService;
import com.cabbooking.service.RefreshTokenService;
import com.cabbooking.service.UserService;
import com.cabbooking.service.impl.UserDetailsServiceImpl;

import com.cabbooking.exception.TokenRefreshException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import com.cabbooking.model.User;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;


    private final UserDetailsServiceImpl userDetailsService;

    /**
     * POST /api/v1/auth/signup
     */
    @PostMapping("/register/passenger")
    public ResponseEntity<AuthResponse> registerPassenger(@Valid @RequestBody PassengerSignupRequest request) {
        AuthResponse  response = authService.registerPassenger(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new Driver.
     */
    @PostMapping("/signup/driver")
    public ResponseEntity<AuthResponse> registerDriver(@Valid @RequestBody DriverSignupRequest request) {
        AuthResponse response = authService.registerDriver(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user (Driver or Passenger).
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        // The service now returns the COMPLETE response (Access and Refresh Token)
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Logs out the user by deleting the Refresh Token from the DB.
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser(@Valid @RequestBody LogoutRequest request) {
        refreshTokenService.deleteByToken(request.refreshToken());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

    /**
     * Generates a new Access Token using a valid Refresh Token.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration) // Verify expiry
                .map(refreshToken -> {
                    User user = refreshToken.getUser();

                    // Modern Fix: Wrap the User entity in UserPrincipal
                    // This satisfied the JwtService which expects UserDetails
                    UserPrincipal principal = new UserPrincipal(user);

                    // Generate new Access Token
                    String newAccessToken = jwtService.generateToken(principal);

                    return ResponseEntity.ok(
                            new TokenRefreshResponse(newAccessToken, requestRefreshToken)
                    );
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }
}