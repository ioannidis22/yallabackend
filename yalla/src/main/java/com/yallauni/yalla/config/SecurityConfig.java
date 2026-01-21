package com.yallauni.yalla.config;

import com.yallauni.yalla.core.security.JwtAuthenticationFilter;
import com.yallauni.yalla.web.rest.error.RestApiAccessDeniedHandler;
import com.yallauni.yalla.web.rest.error.RestApiAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

/**
 * Security configuration with dual filter chains:
 * 1. API chain (/api/**) - JWT-based, stateless
 * 2. UI chain (/**) - Form login, stateful (for H2 console, etc.)
 */
@Configuration
@EnableMethodSecurity // enables @PreAuthorize
public class SecurityConfig {

        /**
         * API chain for REST endpoints (stateless, JWT).
         */
        @Bean
        @Order(1)
        public SecurityFilterChain apiChain(final HttpSecurity http,
                        final JwtAuthenticationFilter jwtAuthenticationFilter,
                        final RestApiAuthenticationEntryPoint restApiAuthenticationEntryPoint,
                        final RestApiAccessDeniedHandler restApiAccessDeniedHandler) throws Exception {
                // Configure security for API endpoints (JWT-based)
                http
                                .securityMatcher("/api/**")
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .requestMatchers("/api/users/register").permitAll()
                                                // All other API endpoints require authentication
                                                .anyRequest().authenticated())
                                .exceptionHandling(exh -> exh
                                                .authenticationEntryPoint(restApiAuthenticationEntryPoint)
                                                .accessDeniedHandler(restApiAccessDeniedHandler))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable);
                return http.build();
        }

        /**
         * UI chain for web pages and tools.
         */
        @Bean
        @Order(2)
        public SecurityFilterChain uiChain(final HttpSecurity http) throws Exception {
                // Configure security for UI endpoints (stateful, form login)
                http
                                .securityMatcher("/**")
                                .csrf(AbstractHttpConfigurer::disable) // Disable for H2 console
                                .headers(headers -> headers
                                                .addHeaderWriter(
                                                                new XFrameOptionsHeaderWriter(
                                                                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/h2-console/**").permitAll()
                                                // Swagger/OpenAPI
                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html",
                                                                "/swagger-ui/**")
                                                .permitAll()
                                                // Public pages
                                                .requestMatchers("/", "/login", "/register", "/error").permitAll()
                                                // Static resources
                                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                                // Everything else requires authentication
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/profile", true)
                                                .failureUrl("/login?error")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .deleteCookies("JSESSIONID")
                                                .invalidateHttpSession(true)
                                                .permitAll())
                                .httpBasic(AbstractHttpConfigurer::disable);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                // Usage of BCrypt for password hashing
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
                // Get the authentication manager from the configuration
                return config.getAuthenticationManager();
        }
}
