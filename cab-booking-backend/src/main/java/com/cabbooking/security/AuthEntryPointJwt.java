package com.cabbooking.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Component to handle unauthorized (401) errors.
 * This class is triggered by Spring Security's exception handling
 * when an unauthenticated user tries to access a secured resource.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * This method is called whenever an unauthenticated user attempts to access
     * a secured REST endpoint.
     *
     * @param request       The request that resulted in an AuthenticationException
     * @param response      The response to send back
     * @param authException The exception that was thrown
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Log the error for server-side debugging
        logger.error("Unauthorized error: {}", authException.getMessage());

        // Set the response content type to application/json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Set the HTTP status code to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a custom JSON response body for the client
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Authentication required: " + authException.getMessage());
        body.put("path", request.getServletPath());

        // Use ObjectMapper to write the JSON to the response's output stream
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}