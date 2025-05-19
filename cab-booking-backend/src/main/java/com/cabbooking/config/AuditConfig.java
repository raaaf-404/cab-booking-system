package com.cabbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")

public class AuditConfig {
    

    @Bean
    public AuditorAware<String> auditorProvider() {
 // TODO: Replace with actual user from security context
        return () -> Optional.of("system");
        
    }
}
