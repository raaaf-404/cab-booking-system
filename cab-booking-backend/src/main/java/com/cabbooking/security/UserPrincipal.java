package com.cabbooking.security;

import com.cabbooking.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Adapter class that bridges the domain User entity with Spring Security.
 * Allows the framework to interact with our custom user data without direct coupling.
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final User user; // Wrap your domain entity

    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Maps our custom UserRole enums to Spring Security's GrantedAuthority format.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Returns the encrypted password hash
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // We use Email as the unique "Username" identifier
    }

    // --- Security Status Flags ---

    @Override
    public boolean isAccountNonExpired() {
        return true;  // The Account never expires (unless we add subscription logic later)
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getIsActive(); // Locks the account if the user is marked inactive
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Password never expires
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive(); // Prevents login if the user is soft-deleted or banned
    }
}
