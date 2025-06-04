package com.cabbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
    
    public static class AuditorAwareImpl implements AuditorAware<String> {

        @Override
        @NonNull
        public Optional<String> getCurrentAuditor() {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("system");
            }

            Object principal = authentication.getPrincipal();


            String username;
            if (principal instanceof User) { // Spring Security's UserDetails User
                username = ((User) principal).getUsername();
            } else if (principal instanceof String) { // Sometimes the principal is just a String
                username = (String) principal;
            } else {
                return Optional.of("unknown_user_type");
            }

            return Optional.of(username);
        }
    }
}
