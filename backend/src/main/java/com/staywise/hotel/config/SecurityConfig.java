
package com.staywise.hotel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Value("${ADMIN_USERNAME:admin}") private String adminUsername;
    @Value("${ADMIN_PASSWORD:}") private String adminPassword;

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean UserDetailsService userDetailsService(PasswordEncoder encoder) {
        if (adminPassword == null || adminPassword.length() < 16) {
            throw new IllegalStateException("Set ADMIN_PASSWORD to a strong password of at least 16 characters.");
        }
        UserDetails admin = User.withUsername(adminUsername)
                .password(encoder.encode(adminPassword)).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll())
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
