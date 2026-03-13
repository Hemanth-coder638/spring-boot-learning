package com.hemanth.currency_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.domain.AuditorAware;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("system-user");
    }
    //without lambda expression
//        return new AuditorAware<String>() {
//        @Override
//        public Optional<String> getCurrentAuditor() {
//            return Optional.of("system-user");
//        }
//    };

}
