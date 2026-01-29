package com.cabbooking.service.impl;

import com.cabbooking.dto.response.AuthResponse;
import com.cabbooking.dto.request.LoginRequest;
import com.cabbooking.dto.request.PassengerSignupRequest;
import com.cabbooking.dto.request.DriverSignupRequest;

import com.cabbooking.model.User;
import com.cabbooking.model.Passenger;
import com.cabbooking.model.Driver;

import com.cabbooking.repository.DriverRepository;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.repository.PassengerRepository;

import com.cabbooking.model.enums.UserRole;
import com.cabbooking.model.enums.DriverStatus;

import java.util.Set;
import java.util.stream.Collectors;
import com.cabbooking.exception.DuplicateResourceException;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j // For logging
public class AuthService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Handles new user registration.
     */
//    @Transactional
//    public AuthResponse register(PassengerSignupRequest request) {
//        log.info("Attempting to register user: {}", request.email());
//
//        if (userRepository.existsByEmail(request.email())) {
//            throw new DuplicateResourceException("Email already exists");
//        }
//
//        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
//            throw new DuplicateResourceException("Phone number already in use");
//        }
//
//        User user = User.builder()
//                .email(request.email())
//                .password(passwordEncoder.encode(request.password()))
//                .role(request.role())
//                .build();
//
//        userRepository.save(user);
//
//        String jwtToken = jwtService.generateToken(user);
//        return new AuthResponse(jwtToken, user.getEmail(), user.getRole().name());
//    }

    @Transactional
    public AuthResponse registerPassenger(PassengerSignupRequest request) {

        // 1. Create the base User
        User user = createUserAccount(
                request.email(),
                request.password(),
                request.phoneNumber(),
                UserRole.ROLE_PASSENGER
        );

        // 2. Create the Passenger Profile
        Passenger passenger = Passenger.builder()
                .user(user)
                .build();

        passengerRepository.save(passenger);

        // 3. Return Token
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse registerDriver(DriverSignupRequest request) {

        if (driverRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new DuplicateResourceException("License number already registered");
        }

        // 1. Create the base User
        User user = createUserAccount(
                request.email(),
                request.password(),
                request.phoneNumber(),
                UserRole.ROLE_DRIVER
        );

        // 2. Create the Driver Profile with license
        Driver driver = Driver.builder()
                .user(user)
                .licenseNumber(request.licenseNumber())
                .isVerified(false) // Needs admin approval later
                .status(DriverStatus.OFFLINE)
                .build();

        driverRepository.save(driver);

        return generateAuthResponse(user);
    }

    private User createUserAccount(String email, String password, String phoneNumber, UserRole role) {

        if (userRepository.existsByEmail(email))
            throw new DuplicateResourceException("Email taken");

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateResourceException("Phone number already in use");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .build();
        user.addRole(role);
        return userRepository.save(user);
    }

    /**
     * Helper method to wrap a User entity into an AuthResponse with a fresh JWT.
     */
    private AuthResponse generateAuthResponse(User user) {
        log.debug("Generating auth response for user: {}", user.getEmail());

        // Generate the JWT token using our security service
        String jwtToken = jwtService.generateToken(user);

        // Map the Set<UserRole> to Set<String>
        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Defensive check: If no roles exist, provide a default
        if (roleNames.isEmpty()) {
            roleNames.add(UserRole.ROLE_PASSENGER.name());
        }

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .roles(roleNames)
                .build();
    }

    /**
     * Authenticates existing users and returns a JWT.
     */
    public AuthResponse login(LoginRequest request) {
        // This line triggers the actual authentication process in Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getEmail(), user.getRole().name());
    }
}
