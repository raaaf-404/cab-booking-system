package com.cabbooking.service.impl;

import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.exception.DuplicateResourceException;
import com.cabbooking.exception.ResourceNotFoundException;
import com.cabbooking.mapper.UserMapper;
import com.cabbooking.model.User;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import com.cabbooking.model.enums.UserRole;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // For securely hashing passwords
    private final UserMapper userMapper;

    @Override
    public Optional<UserResponse> getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::mapToUserResponse);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findAndValidateDriverById(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        if (!driver.getRoles().contains(UserRole.ROLE_DRIVER)) {
            throw new IllegalArgumentException("User with id " + driverId + " is not a DRIVER.");
        }
        return driver;
    }

    @Override
    public User registerUser(String email, String password, String phoneNumber, UserRole role) {

        if (userRepository.existsByEmail(email))
            throw new DuplicateResourceException("Email taken");

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateResourceException("Phone number already in use");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .build();
        user.addRole(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapper::mapToUserResponse);
    }
}
