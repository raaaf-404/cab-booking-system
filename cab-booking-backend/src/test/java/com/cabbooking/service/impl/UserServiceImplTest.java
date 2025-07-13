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

    @Test
    @DisplayName("Test registration with existing email should throw exception")
    void whenRegisterUser_withExistingEmail_thenThrowsUserAlreadyExistsException() {
        // Arrange
        given(userRepository.findByEmail(registrationRequest.getEmail())).willReturn(Optional.of(new User()));

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(registrationRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with email " + registrationRequest.getEmail() + " already exists.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test registration with existing phone should throw exception")
    void whenRegisterUser_withExistingPhone_thenThrowsUserAlreadyExistsException() {
        // Arrange
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByPhone(registrationRequest.getPhone())).willReturn(Optional.of(new User()));

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(registrationRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with phone " + registrationRequest.getPhone() + " already exists.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test registration with no roles assigns default USER role")
    void whenRegisterUser_withNoRoles_thenAssignsDefaultUserRole() {
        // Arrange
        registrationRequest.setRoles(null);
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByPhone(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertThat(userToSave.getRole()).containsExactly(User.Role.USER);
            return savedUser;
        });
        given(userMapper.mapToUserResponse(any(User.class))).willReturn(userResponse);


        // Act
        userService.registerUser(registrationRequest);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Test registration with specific roles assigns correct roles")
    void whenRegisterUser_withSpecificRoles_thenAssignsCorrectRoles() {
        // Arrange
        registrationRequest.setRoles(Set.of("DRIVER"));
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByPhone(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertThat(userToSave.getRole()).containsExactly(User.Role.DRIVER);
            savedUser.setRole(Set.of(User.Role.DRIVER));
            return savedUser;
        });
        given(userMapper.mapToUserResponse(any(User.class))).willReturn(userResponse);


        // Act
        userService.registerUser(registrationRequest);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Test registration with invalid role assigns default USER role")
    void whenRegisterUser_withInvalidRole_thenAssignsDefaultUserRole() {
        // Arrange
        registrationRequest.setRoles(Set.of("INVALID_ROLE"));
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByPhone(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertThat(userToSave.getRole()).containsExactly(User.Role.USER);
            return savedUser;
        });
        given(userMapper.mapToUserResponse(any(User.class))).willReturn(userResponse);

        // Act
        userService.registerUser(registrationRequest);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Test registration with mixed valid and invalid roles assigns valid roles")
    void whenRegisterUser_withMixedValidAndInvalidRoles_thenAssignsValidRoles() {
        // Arrange
        registrationRequest.setRoles(Set.of("USER", "INVALID_ROLE"));
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByPhone(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertThat(userToSave.getRole()).containsExactly(User.Role.USER);
            return savedUser;
        });
        given(userMapper.mapToUserResponse(any(User.class))).willReturn(userResponse);

        // Act
        userService.registerUser(registrationRequest);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Test registration with empty role set assigns default USER role")
    void whenRegisterUser_withEmptyRoleSet_thenAssignsDefaultUserRole() {
        // Arrange
        registrationRequest.setRoles(Collections.emptySet());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByPhone(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertThat(userToSave.getRole()).containsExactly(User.Role.USER);
            return savedUser;
        });
        given(userMapper.mapToUserResponse(any(User.class))).willReturn(userResponse);

        // Act
        userService.registerUser(registrationRequest);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Test getUserById with a valid ID should return user details")
    void whenGetUserById_withValidId_thenReturnsUserResponse() {
        // Arrange
        Long userId = 1L;
        User foundUser = new User(); // The user object returned by the repository
        foundUser.setId(userId);
        foundUser.setName("Test User");
        foundUser.setEmail("test@example.com");

        UserResponse expectedResponse = new UserResponse(); // The DTO we expect to get back
        expectedResponse.setId(userId);
        expectedResponse.setName("Test User");
        expectedResponse.setEmail("test@example.com");

        // Mocking the repository to return our sample user when findById is called
        given(userRepository.findById(userId)).willReturn(Optional.of(foundUser));

        // Mocking the mapper to convert the User object to a UserResponse
        given(userMapper.mapToUserResponse(foundUser)).willReturn(expectedResponse);

        // Act
        Optional<UserResponse> result = userService.getUserById(userId);

        // Assert
        assertThat(result).isPresent(); // Check that the Optional is not empty
        assertThat(result.get().getId()).isEqualTo(userId); // Verify the ID matches
        assertThat(result.get().getEmail()).isEqualTo(foundUser.getEmail()); // Verify the email matches

        // Verify that the repository and mapper methods were called
        verify(userRepository).findById(userId);
        verify(userMapper).mapToUserResponse(foundUser);
    }

    @Test
    @DisplayName("Test getUserById with a non-existent ID should return empty")
    void whenGetUserById_withNonExistentId_thenReturnsEmpty() {
        // Arrange
        Long userId = 99L; // An ID that we assume does not exist

        // Mocking the repository to return an empty Optional
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // Act
        Optional<UserResponse> result = userService.getUserById(userId);

        // Assert
        assertThat(result).isNotPresent(); // or assertThat(result).isEmpty();

        // Verify that the repository was called but the mapper was never used
        verify(userRepository).findById(userId);
        verify(userMapper, never()).mapToUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Test findByEmail with an existing email should return the user")
    void whenFindByEmail_withExistingEmail_thenReturnsUser() {
        // Arrange
        String email = "test@example.com";
        User foundUser = new User(); // The user object we expect from the repository
        foundUser.setId(1L);
        foundUser.setName("Test User");
        foundUser.setEmail(email);

        // Mocking the repository to return our sample user when findByEmail is called
        given(userRepository.findByEmail(email)).willReturn(Optional.of(foundUser));

        // Act
        Optional<User> result = userService.findByEmail(email);

        // Assert
        assertThat(result).isPresent(); // Verify the Optional contains a value
        assertThat(result.get().getEmail()).isEqualTo(email); // Check if the email matches
        assertThat(result.get().getName()).isEqualTo("Test User"); // Check another field for good measure

        // Verify that the repository's findByEmail method was called exactly once with the correct email
        verify(userRepository).findByEmail(email);
    }
}