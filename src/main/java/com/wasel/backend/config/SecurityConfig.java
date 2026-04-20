package com.wasel.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                //
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔐 Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // 🔓 login/register (مهم جدًا)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 🔓 إذا عندك users CRUD (اختياري)
                        .requestMatchers("/api/users/**").permitAll()

                        // 🔐 incidents (محمي)
                        .requestMatchers("/api/incidents/**")
                        .hasAnyRole("ADMIN", "MODERATOR")

                        // 🔐 أي شيء ثاني لازم token
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}