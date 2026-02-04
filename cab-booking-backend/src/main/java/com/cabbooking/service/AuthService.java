package com.cabbooking.service;

import com.cabbooking.dto.request.LoginRequest;
import com.cabbooking.dto.request.PassengerSignupRequest;
import com.cabbooking.dto.request.DriverSignupRequest;
import com.cabbooking.dto.response.AuthResponse;

/**
 * Service interface for handling authentication and registration logic.
 * Adheres to the Dependency Inversion Principle.
 */
public interface AuthService {
    AuthResponse registerPassenger(PassengerSignupRequest request);
    AuthResponse registerDriver(DriverSignupRequest request);
    AuthResponse login(LoginRequest request);
}
