package com.cabbooking.config;

import com.cabbooking.security.AuthEntryPointJwt;
import com.cabbooking.security.JwtAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import org.springframework.security.config.Customizer;

/**
 * Central security configuration hub for the application.
 * Defines the security filter chain, stateless session management, JWT integration,
 * and fine-grained access control rules for different user roles (Drivers/Passengers).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    // 1. INJECTED: For handling 401 errors
    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .cors(Customizer.withDefaults()) // Apply default CORS settings
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")// Disable CSRF for stateless API
                        .ignoringRequestMatchers("/h2-console/**")// Allow H2 console access
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler) // Handle 401 Unauthorized
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // Public auth endpoints
                        .requestMatchers("/h2-console/**").permitAll()// Public H2 console

                        // Role-based access control
                        .requestMatchers("/api/v1/cabs/**").hasRole("DRIVER")
                        .requestMatchers("/api/v1/bookings/driver/**").hasRole("DRIVER")
                        .requestMatchers("/api/v1/bookings/customer/**").hasRole("PASSENGER")

                        .anyRequest().authenticated() // Secure all other endpoints
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No session cookies
                )
                .headers(headers -> headers
                        .frameOptions( HeadersConfigurer.FrameOptionsConfig::sameOrigin)// Allow H2 frames
                        .xssProtection(xss -> xss
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        )
                        .contentSecurityPolicy(policy -> policy
                                .policyDirectives("frame-ancestors 'self'")
                        )
                )

                .authenticationProvider(authenticationProvider(passwordEncoder(), userDetailsService ))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the AuthenticationProvider to use custom user lookup and password encoding.
     * This bean is the bridge between Spring Security manager and our database-backed UserDetailsService.
     */
    @Bean
    @SuppressWarnings("deprecation") // Suppressing warning for standard Spring Boot 3 security configuration
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService); // Link custom user lookup logic
        authProvider.setPasswordEncoder(passwordEncoder); // Set password validation strategy

        return authProvider;
    }
 
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}