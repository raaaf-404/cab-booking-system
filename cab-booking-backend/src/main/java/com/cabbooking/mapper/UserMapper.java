package com.cabbooking.mapper;

import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User; // Correct import for your User entity
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

        if (user.getRole() != null) {
            userResponse.setRoles(user.getRole()); // User.getRole() returns Set<String>
        }
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }
}
