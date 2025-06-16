package com.cabbooking.service.impl;

import com.cabbooking.dto.request.UserRegistrationRequest;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.exception.UserAlreadyExistsException;
import com.cabbooking.model.User;
import com.cabbooking.repository.UserRepository;
import com.cabbooking.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // For securely hashing passwords

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + registrationRequest.getEmail() + " already exists.");
        }
        if (registrationRequest.getPhone() != null && userRepository.findByPhone(registrationRequest.getPhone()).isPresent()) {
            throw new UserAlreadyExistsException("User with phone " + registrationRequest.getPhone() + " already exists.");
        }

        User user = new User();
        user.setName(registrationRequest.getName());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setPhone(registrationRequest.getPhone());
        user.setIsActive(true);

        // Handle roles
        if (registrationRequest.getRoles() == null || registrationRequest.getRoles().isEmpty()) {
            user.addRole("ROLE_USER"); // Default role
        } else {
            user.setRole(new HashSet<>(registrationRequest.getRoles()));
        }

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Override
    public Optional<UserResponse> getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::mapToUserResponse);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert your application's roles to Spring Security's GrantedAuthority
       Set<GrantedAuthority> authorities = user.getRole().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), user.getIsActive(), true, true, true, authorities);
    }
        

    // Helper method to map User entity to UserResponse DTO
    private UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRoles(user.getRole());
        userResponse.setProfilePicture(user.getProfilePicture());
        userResponse.setIsActive(user.getIsActive());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
     
        return userResponse;
    }

    // Implement other methods defined in UserService interface
    // e.g., updateUserProfile, deleteUser, etc.
}
