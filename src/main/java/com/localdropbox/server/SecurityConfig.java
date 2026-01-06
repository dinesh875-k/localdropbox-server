package com.localdropbox.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF for local file uploads
            .csrf(csrf -> csrf.disable())

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/upload", "/files", "/download/**").hasRole("USER")
                .anyRequest().authenticated()
            )

            // Form-based login
            .formLogin(form -> form
                .defaultSuccessUrl("/", true)
            )

            // Logout handling
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
            );

        return http.build();
    }

    @Bean
    public UserDetailsService users() {

        return new InMemoryUserDetailsManager(
            User.withUsername("alice")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build(),

            User.withUsername("bob")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build(),

            User.withUsername("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
