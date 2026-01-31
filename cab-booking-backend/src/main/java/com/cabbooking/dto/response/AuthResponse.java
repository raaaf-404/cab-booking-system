package com.cabbooking.dto.response;

import lombok.Builder;

import java.util.Set;

@Builder
public record AuthResponse(
        String token,
        String email,
        Set<String> roles
) {}
