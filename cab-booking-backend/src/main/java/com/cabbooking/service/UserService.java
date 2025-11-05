package com.cabbooking.service; 

import com.cabbooking.dto.request.SignupRequest;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;


public interface UserService  {
    UserResponse registerUser(SignupRequest registrationRequest);
    Optional<UserResponse> getUserById(Long userId);
    Optional<User> findByEmail(String email);
    User findAndValidateDriverById(Long driverId);
    Page<UserResponse> getAllUsers(Pageable pageable);
}
