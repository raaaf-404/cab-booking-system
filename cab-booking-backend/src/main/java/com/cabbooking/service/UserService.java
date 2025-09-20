package com.cabbooking.service; 

import com.cabbooking.dto.request.UserRegistrationRequest;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    UserResponse registerUser(UserRegistrationRequest registrationRequest);
    Optional<UserResponse> getUserById(Long userId);
    Optional<User> findByEmail(String email);
    User findAndValidateDriverById(Long driverId);
}
