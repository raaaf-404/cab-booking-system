package com.cabbooking.service.impl;

import com.cabbooking.dto.response.AuthResponse;
import com.cabbooking.dto.request.LoginRequest;
import com.cabbooking.dto.request.PassengerSignupRequest;
import com.cabbooking.dto.request.DriverSignupRequest;

import com.cabbooking.model.User;
import com.cabbooking.model.RefreshToken;

import com.cabbooking.security.UserPrincipal;


import com.cabbooking.model.enums.UserRole;

import java.util.Set;
import java.util.stream.Collectors;

import com.cabbooking.service.AuthService;
import com.cabbooking.service.RefreshTokenService;
import com.cabbooking.service.PassengerService;
import com.cabbooking.service.DriverService;
import com.cabbooking.service.UserService;

import org.springframework.security.core.userdetails.UserDetails;
import com.cabbooking.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j // For logging
public class AuthServiceImpl implements AuthService {


    private final UserService userService;
    private final PassengerService passengerService;
    private final DriverService driverService;
    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse registerPassenger(PassengerSignupRequest request) {
        // 1. Create the base User
        User user = userService.registerUser(
                request.email(),
                request.password(),
                request.phoneNumber(),
                UserRole.ROLE_PASSENGER
        );

        // 2. Delegate Passenger creation to PassengerService
        passengerService.registerPassenger(user, request);

        // 3. Return Token
        return generateAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse registerDriver(DriverSignupRequest request) {
        // 1. Create the base User
        User user = userService.registerUser(
                request.email(),
                request.password(),
                request.phoneNumber(),
                UserRole.ROLE_DRIVER
        );

        // 2. Delegate Driver creation to DriverService
        driverService.registerDriver(user, request);

        // 3. Return Token
        return generateAuthResponse(user);
    }

    /**
     * Authenticates existing users and returns a JWT.
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        // 1. Authenticate returns a fully populated Authentication object
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2. Extract the principal (which is your UserPrincipal)
        // and then get the domain User from it
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        return generateAuthResponse(user);
    }

    /**
     * Generates a fully populated AuthResponse with Access AND Refresh tokens.
     */
    private AuthResponse generateAuthResponse(User user) {
        log.debug("Generating auth response for user: {}", user.getEmail());

        UserDetails principal = new UserPrincipal(user);
        String jwtToken = jwtService.generateToken(principal);

        // 2. GENERATE REFRESH TOKEN HERE
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        if (roleNames.isEmpty()) {
            roleNames.add(UserRole.ROLE_PASSENGER.name());
        }

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .email(user.getEmail())
                .roles(roleNames)
                .build();
    }
}
