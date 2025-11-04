package com.cabbooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for capturing user authentication credentials.
 * Used as the request body for the login endpoint.
 *
 * We use 'record' (a modern Java feature) for its conciseness and
 * immutability, which is ideal for DTOs.
 *
 * @param email    The user's email address. Must be a valid email format and not blank.
 * @param password The user's password. Must not be blank.
 */
public record LoginRequest(

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Password cannot be blank")
        String password
) {
}