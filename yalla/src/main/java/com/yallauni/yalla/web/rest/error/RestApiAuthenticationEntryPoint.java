package com.yallauni.yalla.web.rest.error;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Authentication entry point for REST API that returns JSON error responses.
 * Used to send a 401 Unauthorized response in JSON format when authentication
 * is required.
 */
@Component
public class RestApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // For serializing error response to JSON

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // Set HTTP status to 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Set response type to JSON

        final Map<String, Object> error = new LinkedHashMap<>(); // Error details map
        error.put("timestamp", Instant.now().toString()); // Time of error
        error.put("status", HttpStatus.UNAUTHORIZED.value()); // HTTP status code
        error.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase()); // Status reason
        error.put("message", "Authentication required"); // Error message
        error.put("path", request.getRequestURI()); // Request path

        final String json = OBJECT_MAPPER.writeValueAsString(error);
        response.getWriter().write(json);
    }
}
