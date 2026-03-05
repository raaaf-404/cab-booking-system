package com.cabbooking.dto.response;

import com.cabbooking.model.enums.UserRole;
import lombok.Builder;

import java.util.Set;

@Builder
public record AuthResponse(
        String token,
        String refreshToken,
        String email,
        UserRole role
) {}
