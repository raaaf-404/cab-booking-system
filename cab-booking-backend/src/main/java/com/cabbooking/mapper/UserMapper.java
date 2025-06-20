package com.cabbooking.mapper;

import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User; // Correct import for your User entity
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component // Make it a Spring-managed bean so it can be injected
public class UserMapper {

    public UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setProfilePicture(user.getProfilePicture());
        userResponse.setIsActive(user.getIsActive());

        if (user.getRole() != null && !user.getRole().isEmpty()) {
            userResponse.setRoles(
                user.getRole().stream()
                    .map(User.Role::name) // Converts enum to its string name (e.g., USER, DRIVER)
                    .collect(Collectors.toSet()));
        } else {
            userResponse.setRoles(Collections.emptySet()); // Set to empty set if no roles
        }
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }
}
