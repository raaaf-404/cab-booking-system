package com.cabbooking.dto.response;

import com.cabbooking.model.User.Role;
import java.util.List;

/**
 * Data Transfer Object (DTO) for sending a response after successful authentication.
 *
 * @param accessToken  The JSON Web Token (JWT) generated for the authenticated user.
 * The client will send this token in the 'Authorization' header
 * for all following secured requests.
 * @param refreshToken The token used to get a new access token when the current one expires.
 * @param id           The unique identifier of the logged-in user.
 * @param username     The username of the logged-in user.
 * @param email        The email of the logged-in user.
 * @param roles        The list of roles (e.g., USER, DRIVER) for the user.
 * This is useful for the frontend to determine what UI to display.
 * @param tokenType    The type of token, typically "Bearer".
 */
public record JwtResponse(
        String accessToken,
        String refreshToken,
        Long id,
        String username,
        String email,
        List<String> roles,
        String tokenType
) {
    public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email, List<String> roles) {
        this(accessToken, refreshToken, id, username, email, roles, "Bearer");
    }
}