package com.cabbooking.service.impl;

import com.cabbooking.dto.request.UserRegistrationRequest;
import com.cabbooking.dto.response.UserResponse;
import com.cabbooking.exception.UserAlreadyExistsException;
import com.cabbooking.mapper.UserMapper;
import com.cabbooking.model.User;
import com.cabbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequest registrationRequest;
    private User savedUser;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        registrationRequest = new UserRegistrationRequest();
        registrationRequest.setName("Test User");
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setPhone("1234567890");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(registrationRequest.getName());
        savedUser.setEmail(registrationRequest.getEmail());
        savedUser.setPassword("encodedPassword");
        savedUser.setPhone(registrationRequest.getPhone());
        savedUser.addRole(User.Role.USER);


        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Test User");
        userResponse.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Test registration with valid details should succeed")
    void whenRegisterUser_withValidDetails_thenSucceeds() {
        // Arrange
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByPhone(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.mapToUserResponse(any(User.class))).willReturn(userResponse);

        // Act
        UserResponse result = userService.registerUser(registrationRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(registrationRequest.getEmail());
        verify(userRepository).save(any(User.class));
    }
}