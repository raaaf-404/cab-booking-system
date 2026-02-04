package com.cabbooking.service; 

import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.model.User;
import com.cabbooking.model.enums.UserRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;


public interface UserService  {
    Optional<UserResponse> getUserById(Long userId);
    Optional<User> findByEmail(String email);
    User findAndValidateDriverById(Long driverId);
    User registerUser(String email, String password, String phoneNumber, UserRole role);
    Page<UserResponse> getAllUsers(Pageable pageable);
}
