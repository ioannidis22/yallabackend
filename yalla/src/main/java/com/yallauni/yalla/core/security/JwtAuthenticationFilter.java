// Filter for authentication with JWT (REST API, student-style comment)
package com.yallauni.yalla.core.security;

import io.jsonwebtoken.Claims; // JWT claims
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * JWT authentication filter for REST API requests.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(final JwtService jwtService) {
        if (jwtService == null) {
            throw new NullPointerException("jwtService must not be null");
        }
        this.jwtService = jwtService;
    }

    private void writeError(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter()
                .write("{\"error\": \"invalid_token\", \"message\": \"The provided token is invalid or expired\"}");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        final String path = request.getServletPath();
        // Skip JWT filter for public endpoints
        if (path.equals("/api/v1/auth/client-tokens"))
            return true;
        if (path.startsWith("/api/auth/"))
            return true;
        if (path.startsWith("/h2-console"))
            return true;
        // Only filter API requests
        return !path.startsWith("/api/");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        // No header or not Bearer? -> Let the request continue unauthenticated.
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);
        try {
            final Claims claims = this.jwtService.parse(token);
            final String subject = claims.getSubject();
            final Collection<String> roles = (Collection<String>) claims.get("roles");

            // Convert roles to GrantedAuthority
            final List<GrantedAuthority> authorities;
            if (roles == null) {
                authorities = List.of();
            } else {
                authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .map(auth -> (GrantedAuthority) auth)
                        .toList();
            }

            // Create authenticated user
            final User principal = new User(subject, "", authorities);
            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception ex) {
            LOGGER.warn("JWT authentication failed: {}", ex.getMessage());
            this.writeError(response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
