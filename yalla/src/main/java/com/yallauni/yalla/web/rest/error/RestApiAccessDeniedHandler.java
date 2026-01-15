package com.yallauni.yalla.web.rest.error;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Access denied handler for REST API that returns JSON error responses.
 * Used to send a 403 Forbidden response in JSON format when access is denied.
 */
@Component
public class RestApiAccessDeniedHandler implements AccessDeniedHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // For serializing error response to JSON

    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value()); // Set HTTP status to 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Set response type to JSON

        final Map<String, Object> error = new LinkedHashMap<>(); // Error details map
        error.put("timestamp", Instant.now().toString()); // Time of error
        error.put("status", HttpStatus.FORBIDDEN.value()); // HTTP status code
        error.put("error", HttpStatus.FORBIDDEN.getReasonPhrase()); // Status reason
        error.put("message", "Access denied"); // Error message
        error.put("path", request.getRequestURI()); // Request path

        final String json = OBJECT_MAPPER.writeValueAsString(error);
        response.getWriter().write(json);
    }
}
