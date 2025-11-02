package com.cabbooking.service.impl;

import com.cabbooking.model.User;
import com.cabbooking.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This service is responsible for loading user-specific data by email
 * for authentication and authorization purposes.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Injects the UserRepository using constructor-based dependency injection.
     * This is the recommended approach for required dependencies.
     *
     * @param userRepository The repository for accessing User data.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address (which is used as the "username" in this system).
     *
     * @param email The email address of the user to load.
     * @return A UserDetails object containing the user's credentials and authorities.
     * @throws UsernameNotFoundException if no user is found with the given email.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable("users") // Caches the result of this method based on the email.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Fetch the user from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );

        // 2. Map the user's roles to Spring Security's GrantedAuthority
        // We must prefix with "ROLE_" for hasRole() expressions to work correctly.
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        // 3. Construct and return the Spring Security User object
        // This object encapsulates all necessary authentication and authorization details.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),          // username
                user.getPassword(),       // password
                user.getIsActive(),       // enabled
                true,               // accountNonExpired
                true,               // credentialsNonExpired
                true,               // accountNonLocked
                authorities               // authorities
        );
    }
}