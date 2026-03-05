package com.cabbooking.dto.response;

import com.cabbooking.model.enums.UserRole;
import java.time.LocalDateTime;

/**
 * Modern Java Record for User Response.
 * Immutability by default ensures thread-safety and data integrity.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        String phoneNumber,
        String profilePicture,
        Boolean isActive,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}