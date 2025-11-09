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
                // 2. EDITED: Disable CSRF for stateless API, but configure for H2
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // Ignore for your stateless API
                        .ignoringRequestMatchers("/h2-console/**") // Ignore for H2 console
                )
                // 3. ADDED: Custom 401 Unauthorized handler
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                // 4. EDITED: Updated authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public auth endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // H2 Console (for development only)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Your existing app-specific rules
                        .requestMatchers("/api/v1/cabs/**").hasRole("DRIVER")
                        .requestMatchers("/api/v1/bookings/driver/**").hasRole("DRIVER")
                        .requestMatchers("/api/v1/bookings/customer/**").hasRole("CUSTOMER")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                // We are stateless, so no sessions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. ADDED: Headers required for H2 console to work
                .headers(headers -> headers
                        .frameOptions( HeadersConfigurer.FrameOptionsConfig::sameOrigin) // Allows H2 console frames
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

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
 
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}