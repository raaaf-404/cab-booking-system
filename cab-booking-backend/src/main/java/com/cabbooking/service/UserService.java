package com.cabbooking.service; 

import com.cabbooking.dto.request.UserRegistrationRequest;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User;
import java.util.Optional;


public interface UserService {
    UserResponse registerUser(UserRegistrationRequest registrationRequest);
    Optional<UserResponse> getUserById(Long userId);
    Optional<User> findByEmail(String email);
    
}
