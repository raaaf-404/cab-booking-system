package com.cabbooking.dto.response;

import com.cabbooking.model.User.Role;

/**
 * Data Transfer Object (DTO) for sending a response after successful authentication.
 *
 * @param jwtToken The JSON Web Token (JWT) generated for the authenticated user.
 * The client will send this token in the 'Authorization' header
 * for all following secured requests.
 * @param userId   The unique identifier of the logged-in user.
 * @param role     The role of the user (e.g., USER, DRIVER). This is very
 * useful for the frontend to determine what UI to display.
 */
public record AuthenticationResponse(
        String jwtToken,
        Integer userId,
        Role role
) {
}
