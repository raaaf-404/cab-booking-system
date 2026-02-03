package com.cabbooking.dto.request;

import jakarta.validation.constraints.*;

/**
 * DTO for user registration.
 * Using Java Record for immutability and conciseness.
 */
public record DriverSignupRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits)")
        String phoneNumber,

        @NotBlank(message = "License number is required")
        String licenseNumber
) {}