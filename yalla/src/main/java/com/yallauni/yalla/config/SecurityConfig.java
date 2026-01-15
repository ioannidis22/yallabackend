package com.yallauni.yalla.config;

// Custom filter for JWT authentication
import com.yallauni.yalla.core.security.JwtAuthenticationFilter;
// Custom handler for access denied errors in REST API
import com.yallauni.yalla.web.rest.error.RestApiAccessDeniedHandler;
// Custom handler for authentication errors in REST API
import com.yallauni.yalla.web.rest.error.RestApiAuthenticationEntryPoint;

// Spring bean definition annotation (already commented elsewhere)
import org.springframework.context.annotation.Bean;
// Marks this class as a configuration class (already commented elsewhere)
import org.springframework.context.annotation.Configuration;
// Used to set order of security filter chains
import org.springframework.core.annotation.Order;
// Used to get the authentication manager bean
import org.springframework.security.authentication.AuthenticationManager;
// Used to get authentication configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// Enables method-level security annotations (e.g., @PreAuthorize)
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// Main class for configuring HTTP security
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// Used to customize HTTP security config
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// Controls how sessions are created and managed
import org.springframework.security.config.http.SessionCreationPolicy;
// BCrypt implementation for password encoding
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// Interface for password encoding
import org.springframework.security.crypto.password.PasswordEncoder;
// Represents a security filter chain
import org.springframework.security.web.SecurityFilterChain;
// Filter for username/password authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Used to set X-Frame-Options header
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
        // Configure security for API endpoints (stateless, JWT-based)
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
     * UI chain for web pages and tools (stateful, cookie-based).
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
                                new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .authorizeHttpRequests(auth -> auth
                        // H2 Console
                        .requestMatchers("/h2-console/**").permitAll()
                        // Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
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
                // Use BCrypt for password hashing
                return new BCryptPasswordEncoder();
        }

    @Bean
        public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
                // Get the authentication manager from the configuration
                return config.getAuthenticationManager();
        }
}
